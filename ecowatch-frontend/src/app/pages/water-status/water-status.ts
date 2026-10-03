import {Component, OnInit} from '@angular/core';
import {
  LocationSummary,
  RessourceType,
  WaterRessource,
  WaterRessourceRequest,
  WaterRessourcesSeuilRequest,
  WaterRessourceStats,
  WaterRessourceStatus
} from '../../core/models/models';
import {AggregationService} from '../../core/services/AggregationService';
import {DatePipe, DecimalPipe, NgClass} from '@angular/common';
import * as L from 'leaflet'
import {WaterAdminService} from '../../core/services/WaterAdminService';
import {AuthService} from '../../core/services/AuthService';
import {FormsModule} from '@angular/forms';

interface TypeMeta{
  label:string;
  icon:string;
}
const Types:Record<RessourceType, TypeMeta>={
  [RessourceType.BARRAGE]:{label:'Barrage',icon:'🏞'},
  [RessourceType.LAC]:{label:'Lac',icon:'🌊'},
  [RessourceType.RIVIERE]:{label:'Riviere',icon:'〰️' },
  [RessourceType.NAPPE_PHREATIQUE]:{label:'Nappe Phreatique',icon:'⬤'}
};
const StatusLabel:Record<WaterRessourceStats, string>={
  BAS:'Bas',
  NORMAL:'Normal',
  ELEVE:'Eleve',
  CRITIQUE:'Critique'
}
const StatusColor:Record<WaterRessourceStats, string>={
  CRITIQUE:'#A3372B',
  NORMAL:'#1F4D3D',
  BAS:'#B5701E',
  ELEVE:'#2C5F7C'
}
@Component({
  selector: 'app-water-status',
  imports: [
    DatePipe,
    DecimalPipe,
    NgClass,
    FormsModule
  ],
  templateUrl: './water-status.html',
  styleUrl: './water-status.css',
})
export class WaterStatus implements OnInit{
  data:WaterRessourceStatus |null=null; // les donnes brutes recues du serveur
  loading=true;
  allRessources:WaterRessource[]=[]  //un grand tableau où on va mettre toutes les ressources d'eau pour faciliter le filtrage
  activeType:RessourceType|'all'='all' // le filtrage du type actif
  searchText='' //le texte tape dans la barre de recherche
  pinned:WaterRessource|null=null //la ressource dont on a cliqué sur la ligne pour voir les details
  currentHover:WaterRessource|null=null //la ressource survolee par la souris
  triMode:'none'|'critical-first'|'full-first'="none"
  currentView:'list'|'map'='list'
  statusLabel=StatusLabel
  // ── Droits ADMIN — calculé une fois à la construction (le rôle ne change pas en cours de session) ──
  readonly isAdmin:boolean
  // ── État du formulaire ADMIN (création / édition d'une ressource) ──
  showForm = false
  editingId: number | null = null
  formModel: WaterRessourceRequest = this.emptyForms()
  formError = ''
  formSaving = false
  // ── État du formulaire de seuils ADMIN ──
  seuilTargetId: number | null = null
  seuilModel: WaterRessourcesSeuilRequest = { seuilBas: 40, seuilCritique: 20 }
  seuilSaving = false
  seuilError = ''
  // ── Villes disponibles pour le sélecteur de localisation du formulaire ADMIN ──
  cities: { id: number; name: string }[] = [];

  deleteError = '';

  private map:L.Map|null=null
  private markers:L.Marker[]=[] // la liste des marqueurs pose dans la carte
  private mapInitialized=false //pour ne pas recrer la carte a chaque changement
  constructor(private aggregation:AggregationService,
              private waterAdmin:WaterAdminService,
              private authservice:AuthService) {
    this.isAdmin=this.authservice.hasRole('ADMIN')
  }


  ngOnInit(){
       this.loadWaterStatus()
    if (this.isAdmin)return this.loadcities()
    }
  private loadcities(){
    this.aggregation.getLocations().subscribe({
      next:(location:LocationSummary[])=>
      {this.cities=location.map((l)=>({id:l.id,name:l.nameCity}))
      },
      error:(err)=>console.error('Erreur chargement villes: '+err)
    })
  }
  loadWaterStatus(){
    this.aggregation.getWaterStatus().subscribe({
      next:d=>{
        this.data=d;
        // on met tous les ressources dans un seul tableau
        this.allRessources=[
          ...(d.barrages||[]),
            ...(d.riviere||[]),
          ...(d.lacs||[]),
          ...(d.nappe||[])
        ]
        this.loading=false;
        if (this.currentView==='map')return this.renderMapMarkers()
      },
      error:(err) => {
        console.error('Erreur water status:', err);
        this.loading = false;
      }
    });
  }
   //compter combien il y a des ressources pour chaque type
  counts():Record<string, number>{
    const c:Record<string,number>={all:this.allRessources.length};
    this.typeKeys().forEach((t)=>
    c[t]=this.allRessources.filter((r)=>r.ressourceType===t).length)
    return c;
  }

  typeKeys():RessourceType[]{
    return Object.values(RessourceType)
  }

  // declenche quand l'utilisateur clique sur les choix tous, barrages...
  setActiveType(type:RessourceType|'all'):void{
    this.activeType=type
    this.pinned=null
    if (this.currentView === 'map') this.renderMapMarkers();
  }
  //------ triages des ressources -------
  // recherche par nom, ville, ou cle de type dans la barre de recherche
  search(ressource:WaterRessource):boolean{
    const q=this.searchText.trim().toLowerCase()
    if (!q){
      return this.activeType==='all'||ressource.ressourceType===this.activeType
    }
    const typeKeyWord=(Object.entries(Types)as [RessourceType,TypeMeta][]).find(
      ([key,meta])=>meta.label.toLowerCase().startsWith(q)||key.toLowerCase().includes(q)
    )
    // Si on a trouvé un type correspondant et que le mot tapé est assez long, on filtre par ce type
    if (typeKeyWord && q.length>2){
      return ressource.ressourceType===typeKeyWord[0]
    }
    //Sinon, recherche classique : est-ce que le nom ou la ville contient le mot tapé
    return (ressource.name.toLowerCase().includes(q)||
      (ressource.location?.nameCity||'').toLowerCase().includes(q))
  }

  // type de triage
  sortedRessource(list:WaterRessource[]):WaterRessource[]{
    if (this.triMode==='critical-first'){
      const order:Record<WaterRessourceStats,number>={
        CRITIQUE:0,
        BAS:1,
        NORMAL:2,
        ELEVE:3
      }
      return [...list].sort(
        (a,b)=> {
          return order[a.fillStatus] - order[b.fillStatus] || a.fillPercentage - b.fillPercentage;
        }
      )
    }
    if (this.triMode === 'full-first') {
      return [...list].sort((a, b) => b.fillPercentage - a.fillPercentage);
    }
    return list;
  }
  get filtredRessources():WaterRessource[]{
    const base=this.allRessources.filter(
      (r)=>this.activeType==='all'||r.ressourceType===this.activeType&&this.search(r))
    return this.sortedRessource(base)
  }
  onSortChange(mode: string): void {
    this.triMode = mode as 'none' | 'critical-first' | 'full-first';
    if (this.currentView === 'map') this.renderMapMarkers();
  }

  onSearchChange(value: string): void {
    this.searchText = value;
    this.pinned = null;
    if (this.currentView === 'map') this.renderMapMarkers();
  }
  //----- epinglage des ressouces -------
  onRowEnter(r:WaterRessource):void{
    this.currentHover=r
  }
  onRowLeave():void{
    this.currentHover=null
  }
  //Cliquer une fois → ça s'épingle. Cliquer une deuxième fois sur la même ligne → ça se désépingle.
  onRowClick(r:WaterRessource):void{
    this.pinned=this.pinned===r ?null:r
  }
  //pour savoir à quoi afficher sur la fiche de détails
  get displayedRessource(): WaterRessource | null {
    return this.pinned || this.currentHover;
  }

  //---------- vue :list /map -------------
  private initMap(): void {
    this.map = L.map('resMap', { zoomControl: true }).setView([32.5, -6.5], 6);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '',
    }).addTo(this.map);
    this.mapInitialized = true;
  }
  private renderMapMarkers() {
    if (!this.map) return;
    this.markers.forEach((m) => this.map!.removeLayer(m))
    this.markers = [];

    this.filtredRessources.forEach((r) => {
      if (!r.location) return;
      const color = StatusColor[r.fillStatus] || StatusColor.NORMAL;
      const icon = L.divIcon({
        html: `<div style="width:15px;height:15px;background:${color};border:2.5px solid #FBF6EA;border-radius:50%;box-shadow:0 2px 8px rgba(0,0,0,.3)"></div>`,
        className: '',
        iconAnchor: [8, 8],
      });
      const marker = L.marker([r.location.latitude, r.location.longitude], { icon }).addTo(this.map!);
      marker.bindPopup(
        `<div style="font-family:Fraunces,serif;font-weight:600;font-size:13px">${Types[r.ressourceType].icon} ${r.name}</div>
         <div style="font-size:11px;color:#7A6F58;margin:2px 0">${r.location.nameCity}</div>
         <div style="font-family:'IBM Plex Mono',monospace;font-size:12px;color:${color}">${r.fillPercentage.toFixed(1)}% — ${StatusLabel[r.fillStatus]}</div>`
      );
      this.markers.push(marker);
    });
  }
  setView(view:'list'|'map'):void{
    this.currentView=view;
    if (view === 'map') {
      setTimeout(() => {
        if (!this.mapInitialized) this.initMap();
        this.map?.invalidateSize();
        this.renderMapMarkers();
      }, 50);
    }
  }

    getStatusClass(status:WaterRessourceStats): string {
      switch (status) {
        case "CRITIQUE": return 'danger';
        case "BAS"  :   return 'warning';
        case "NORMAL":   return 'success';
        case "ELEVE":    return 'info';
        default:         return 'info';
      }
    }

    getGaugeClass(status: string): string {
      switch (status) {
        case 'CRITIQUE': return 'ew-gauge-danger';
        case 'BAS':      return 'ew-gauge-warning';
        case 'ELEVE':    return 'ew-gauge-info';
        default:         return 'ew-gauge-success';
      }
    }
  typeIcon(type: RessourceType): string {
    return Types[type]?.icon || '';
  }

  typeLabel(type: RessourceType): string {
    return Types[type]?.label || type;
  }

  formatCoords(lat: number, lon: number): string {
    const latDir = lat >= 0 ? 'N' : 'S';
    const lonDir = lon >= 0 ? 'E' : 'O';
    return `${Math.abs(lat).toFixed(2)}°${latDir} . ${Math.abs(lon).toFixed(2)}°${lonDir}`;
  }

  // ------------ les actions d'admin :create/update/delete/seuil
  private emptyForms():WaterRessourceRequest{
    return {
      name:'',
      capaciteMax:0.0,
      currentLevel:0.0,
      ressourceType:RessourceType.BARRAGE,
      locationId:0
    }
  }

  openCreateForm(){
    this.editingId=null
    this.formModel=this.emptyForms()
    this.formError=''
    this.showForm=true
  }

  openEditForm(r:WaterRessource){
    this.editingId=r.id
    this.formModel={
      name:r.name,
      capaciteMax:r.capaciteMax,
      currentLevel:r.currentLevel,
      ressourceType:r.ressourceType,
      locationId:r.location?.id ?? 0
    }
    this.formError=''
    this.showForm=true
  }

  closeForm(): void {
    this.showForm = false;
    this.formError = '';
  }

  submitForm(){
    if (!this.formModel.name.trim()||!this.formModel.locationId){
      this.formError='Le nom et la ville sont obligatoires.'
    }
    this.formSaving=true
    this.formError=''

    const request=this.editingId
      ?this.waterAdmin.update(this.editingId,this.formModel)
      :this.waterAdmin.create(this.formModel)
    request.subscribe({
      next:()=>{
        this.formSaving=false
        this.showForm=false
        this.loadWaterStatus()
      },
      error:(err)=>{
        this.formSaving=false
        this.formError=err.status===403
          ? 'Action réservée aux administrateurs.'
          : "Erreur lors de l'enregistrement. Vérifie les champs et réessaie.";
        console.error('Erreur formulaire ressource eau:', err);
      }
    })
  }

  deleteRessource(r:WaterRessource){
    if (!confirm(`Supprimer definitivement ${r.name} ?`))return;
    this.deleteError=''
    this.waterAdmin.delete(r.id).subscribe({
      next:()=>{
        if (this.pinned?.id===r.id) this.pinned=null
        this.loadWaterStatus()
      },
      error: (err) => {
        this.deleteError = err.status === 403
          ? 'Action réservée aux administrateurs.'
          : 'Erreur lors de la suppression.';
        console.error('Erreur suppression ressource eau:', err);
      }
    })
  }

  openSeuilForm(r: WaterRessource): void {
    this.seuilTargetId = r.id;
    this.seuilModel = {
      seuilBas: r.seuilBas ?? 40,
      seuilCritique: r.seuilCritique ?? 20,
    };
    this.seuilError = '';
  }

  closeSeuilForm(): void {
    this.seuilTargetId = null;
    this.seuilError = '';
  }

  submitSeuil(): void {
    if (this.seuilTargetId == null) return;
    if (this.seuilModel.seuilCritique >= this.seuilModel.seuilBas) {
      this.seuilError = 'Le seuil critique doit être inférieur au seuil bas.';
      return;
    }
    this.seuilSaving = true;
    this.seuilError = '';
    this.waterAdmin.updateSeuil(this.seuilTargetId, this.seuilModel).subscribe({
      next: () => {
        this.seuilSaving = false;
        this.seuilTargetId = null;
        this.loadWaterStatus();
      },
      error: (err) => {
        this.seuilSaving = false;
        this.seuilError = err.status === 403
          ? 'Action réservée aux administrateurs.'
          : 'Erreur lors de la mise à jour des seuils.';
        console.error('Erreur seuils ressource eau:', err);
      },
    });
  }




}
