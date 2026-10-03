import {Component, OnInit} from '@angular/core';
import {DashBoardResponse, GlobalCitySummary} from '../../core/models/models';
import {AggregationService} from '../../core/services/AggregationService';
import {FormsModule} from '@angular/forms';
import {DatePipe, DecimalPipe, NgClass} from '@angular/common';
import * as L from 'leaflet'
import {CityImageService} from '../../core/services/CityImageService';


@Component({
  selector: 'app-dashboard',
  imports: [
    FormsModule,
    DecimalPipe,
    DatePipe,
    NgClass,
    //NgClass,

  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})

export class Dashboard implements OnInit {

  // Données du dashboard de la ville sélectionnée
  dashboard: DashBoardResponse | null = null;

  // Résumé global de toutes les villes
  summary: GlobalCitySummary | null = null;

  // Affiche le spinner pendant le chargement
  loading = true;

  // ID de la ville sélectionnée (1 = Casablanca par défaut)
  selectedLocationId = 1;
  // Pour la recherche d'une ville
  searchQuery:string='';
  filteredCities:{id:number,name:string}[]=[];
  showDropDown=false;
  highlightedIndex = 0;

  searchLoading = false;
  searchError = '';

  // ── Grille "Toutes les villes" : filtre + affichage limité ──
  cityGridFilter = '';
  showAllCitiesGrid = false;
  readonly citiesGridPageSize = 12;

  // ── Mini-carte Leaflet ──
  private map: L.Map | null=null
  private marker :L.Marker |null=null

  // Liste des villes disponibles
  cities = [
    {id: 1, name: "Casablanca"},
    {id: 2, name: "Rabat"},
    {id: 3, name: "Fès"},
    {id: 4, name: "Marrakech"},
    {id: 5, name: "Agadir"},
    {id: 6, name: "Tanger"},
    {id: 7, name: "Meknès"},
    {id: 8, name: "Oujda"},
    {id: 9, name: "Al Hoceima"},
    {id: 10, name: "Oued Zem"},
    {id: 11, name: "Taza"},
    {id: 12, name: "Azizal"},
    {id: 13, name: "Settat"},
    {id: 14, name: "Khouribga"},
    {id: 15, name: "Béni Mellal"},
    {id: 16, name: "Safi"},
    {id: 17, name: "Kenitra"},
    {id: 18, name: "Berkane"},
    {id: 19, name: "Taounate"},
    {id: 20, name: "Errachidia"},
    {id: 21, name: "Dakhla"},
    {id: 22, name: "Laayoune"},
    {id: 23, name: "Ouarzazate"},
    {id: 24, name: "El Kelaa des Sraghna"},
    {id: 25, name: "Zagora"},
    {id: 26, name: "Taroudant"},
    {id: 27, name: "Tiznit"},
    {id: 28, name: "Tétouan"},
    {id: 29, name: "Larache"},
    {id: 30, name: "Nador"},
    {id: 31, name: "Khenifra"},
    {id: 32, name: "Taourirt"},
    {id: 33, name: "Guelmim"},
    {id: 34, name: "Guercif"},
    {id: 35, name: "Khemisset"},
    {id: 36, name: "Sale"},
    {id: 37, name: "Benslimane"},
    {id: 38, name: "Ifrane"},
    {id: 39, name: "El Jadida"},
    {id: 40, name: "Erfoud"},
    {id: 41, name: "Tinghir"},

  ];

  constructor(private aggregationService: AggregationService,
              private cityImageService: CityImageService) {}

  ngOnInit() {
    this.loadLocationsAndDashboard()
    this.loadGlobalSummary();
  }

  loadDashboard() {
    this.loading = true;
    this.destroyMap();
    this.aggregationService.getDashboard(this.selectedLocationId).subscribe({
      next: (data) => {
        this.dashboard = data;
        if (this.dashboard.forecastData) {
          const seen = new Set<string>();
          this.dashboard.forecastData = this.dashboard.forecastData
            .filter(f => !f.expired)// garde seulement les futures
            .filter(f => {
              const dateKey = f.date.substring(0, 10);  // "2026-06-18"
              if (seen.has(dateKey)) return false;
              seen.add(dateKey);
              return true;
            })
            .slice(0, 5);                   // maximum 5
        }

        this.loading = false;
        this.cityPhoto(this.dashboard.cityName);
        requestAnimationFrame(()=>{
          requestAnimationFrame(()=>this.initOrUpdateMap())
        })
      },
      error: (err) => {
        console.error('Erreur dashboard:', err);
        this.loading = false;
      }
    });
  }

  loadGlobalSummary() {
    this.aggregationService.getGlobalSummary().subscribe({
      next: (data) => this.summary = data,
      error: (err) => console.error('Erreur summary:', err)
    });
  }
  loadLocationsAndDashboard() {
    this.loading = true;
    this.aggregationService.getLocations().subscribe({
      next: (data) => {
        this.cities = data.map((location: { id: any; nameCity: any }) => ({
          id: location.id,
          name: location.nameCity
        }));
        // Une fois les villes chargées, on charge le dashboard de la ville par défaut
        this.loadDashboard();
      },
      error: () => {
        // Fallback sur la liste fixe, puis charge quand même
        this.cities = [
          { id: 1, name: 'Casablanca' },
          { id: 2, name: 'Rabat' },
          { id: 3, name: 'Fès' },
          { id: 4, name: 'Marrakech' },
          { id: 5, name: 'Agadir' },
          { id: 6, name: 'Tanger' },
          { id: 7, name: 'Meknes' },
        ];
        this.loadDashboard();
      }
    });
  }
  selectCity(id: number) {
    if (id === this.selectedLocationId) {
      // Même ville — recharge quand même
      this.loadDashboard();
      return;
    }
    this.selectedLocationId = id;
    this.dashboard = null;
    this.loading = true;
    this.loadDashboard();
  }

  // ── Grille "Toutes les villes" ──
  getFilteredGridCities() {
    if (!this.summary) return [];
    const q = this.cityGridFilter.trim().toLowerCase();
    const filtered = q
      ? this.summary.cities.filter(c => c.city.toLowerCase().includes(q))
      : this.summary.cities;
    return this.showAllCitiesGrid ? filtered : filtered.slice(0, this.citiesGridPageSize);
  }

  getFilteredCityCount(): number {
    if (!this.summary) return 0;
    const q = this.cityGridFilter.trim().toLowerCase();
    return q
      ? this.summary.cities.filter(c => c.city.toLowerCase().includes(q)).length
      : this.summary.cities.length;
  }
  toggleShowAllCities() {
    this.showAllCitiesGrid = !this.showAllCitiesGrid;  }


  // Retourne le nom du capteur à partir de son ID
  getSensorName(sensorId: number): string {
    const sensor = this.dashboard?.sensor.find(s => s.id === sensorId);
    return sensor?.name ?? 'Capteur ' + sensorId;
  }
  //filtrer les villes
  onSearch(query:string){
    this.searchQuery=query;
    if (query.trim().length==0){
      this.filteredCities=[];
      this.showDropDown=false;
      return;
    }
    this.filteredCities=this.cities.filter(c=>
      c.name.toLowerCase().includes(query.toLowerCase()));
    this.showDropDown=this.filteredCities.length>0;
    this.highlightedIndex = this.filteredCities.length > 0 ? 0 : -1;
  }


  onSearchKeydown(event: KeyboardEvent): void {
    if (!this.showDropDown || this.filteredCities.length === 0) return;

    if (event.key === 'ArrowDown') {
      event.preventDefault();
      this.highlightedIndex = (this.highlightedIndex + 1) % this.filteredCities.length;
      this.scrollHighlightedIntoView();
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      this.highlightedIndex =
        (this.highlightedIndex - 1 + this.filteredCities.length) % this.filteredCities.length;
      this.scrollHighlightedIntoView();
    } else if (event.key === 'Enter' && this.highlightedIndex >= 0) {
      event.preventDefault();
      this.selectCityFromSearch(this.filteredCities[this.highlightedIndex]);
      this.highlightedIndex = -1;
    } else if (event.key === 'Escape') {
      this.showDropDown = false;
      this.highlightedIndex = -1;
    }
  }

  private scrollHighlightedIntoView(): void {
    setTimeout(() => {
      const el = document.querySelector('.db-dropdown-item.highlighted');
      el?.scrollIntoView({ block: 'nearest' });
    });
  }
  //Appelée quand l'utilisateur clique sur une ville dans la liste
  selectCityFromSearch(city:{id:number,name:string}){
    this.searchQuery=city.name;
    this.showDropDown=false;
    this.selectedLocationId = city.id;
    this.dashboard = null;
    this.loading = true;
    this.loadDashboard();
  }
  // Nouvelle méthode — appelée quand l'utilisateur tape Entrée ou clique Rechercher
  searchAndLoad() {
    const query = this.searchQuery.trim();
    if (!query) return;

    // 1. Cherche d'abord dans la liste locale (les villes déjà en base)
    const found = this.cities.find(c =>
      c.name.toLowerCase() === query.toLowerCase()
    );

    if (found) {
      // Ville trouvée localement → charge directement par ID
      this.selectCityFromSearch(found);
      return;
    }

    // 2. Ville inconnue localement → appelle le backend pour chercher/créer
    this.searchLoading = true;
    this.searchError = '';
    this.dashboard = null;

    this.aggregationService.searchCity(query).subscribe({
      next: (weatherData) => {
        this.searchLoading = false;
        // Le backend a trouvé/créé la ville
        // On recharge les locations pour avoir le nouvel ID
        this.aggregationService.getLocations().subscribe(locations => {
          this.cities = locations.map((l: any) => ({
            id: l.id,
            name: l.nameCity
          }));
          // Cherche maintenant la ville dans la liste mise à jour
          const newCity = this.cities.find(c =>
            c.name.toLowerCase() === query.toLowerCase()
          )|| (weatherData?.location
            ? this.cities.find(c => c.id === weatherData.location.id)
            : null);
          if (newCity) {
            this.selectedLocationId = newCity.id;
            this.searchQuery = newCity.name;
            this.loadDashboard();
          }else {
            // Ville créée mais, pas encore dans les locations → on affiche les données directement
            this.searchError = `Données récupérées mais ville non indexée. Réessayez.`;
          }
        });
      },
      error: (err) => {
        this.searchLoading = false;
        this.searchError = `Ville "${query}" introuvable. Vérifiez le nom et réessayez.`;
        console.error('Erreur recherche ville:', err);
      }
    });
  }

  // Ferme le dropdown si clic ailleurs
  closeDropDown(){
    setTimeout(()=>this.showDropDown=false,300);
  }

  // Retourne la classe Bootstrap selon le statut
  getStatusBadge(status: string): string {
    switch (status) {
      case 'NORMAL':  return 'green';
      case 'WARNING': return 'ochre';
      case 'DANGER':  return 'red';
      default:        return '';
    }
  }

  // Retourne l'emoji météo selon la description
  getWeatherIcon(description: string): string {
    const d = description?.toLowerCase() ?? '';
    if (d.includes('pluie') || d.includes('rain'))   return 'wicon-rainy';
    if (d.includes('nuage') || d.includes('cloud'))  return 'wicon-cloudy';
    if (d.includes('orage') || d.includes('storm'))  return 'wicon-stormy';
    if (d.includes('neige') || d.includes('snow'))   return 'wicon-snowy';
    if (d.includes('brouillard') || d.includes('fog')) return 'wicon-fogy';
    return 'wicon-sunny';
  }

  // Classe de couleur pour l'IQA moyen (seuils génériques : 0-50 bon, 51-100 modéré, 100+ danger)
  colorIQAClass(value: number | null | undefined): string {
    if (value == null) return '';
    if (value <= 50) return 'green';
    if (value <= 100) return 'ochre';
    return 'red';
  }

  // Classe de couleur pour les PM2.5 moyennes (seuils génériques µg/m³ : 0-35 bon, 36-75 modéré, 75+ danger)
  colorPoussiereClass(value: number | null | undefined): string {
    if (value == null) return '';
    if (value <= 35) return 'green';
    if (value <= 75) return 'ochre';
    return 'red';
  }

  // Photo de la ville affichée en fond du hero, ou null
  cityPhotoUrl:string|null=null;
  cityPosition:string='center';
  private cityPhoto(cityName:string){
    const config=this.cityImageService.getPhoto(cityName);
    if (!config){
      this.cityPhotoUrl=null;
      return;
    }
    this.cityPosition=config.position ?? 'center';
    const probe=new Image();
    probe.onload=()=>{this.cityPhotoUrl=config.url};
    probe.onerror = () => {
      console.warn(`[CityImageService] Photo introuvable ou cassée pour "${cityName}" → dégradé utilisé.`, config.url);
      this.cityPhotoUrl = null;
    };
    probe.src=config.url;
  }

  // Coordonnées formatées pour le badge sous la mini-carte, ex : "33.59°N · 7.62°O"
  formatCoords(lat:number,lon:number):string{
    const latDir=lat>=0 ?"N":"S";
    const lonDir=lon>=0 ?"E": "O";
    return `${Math.abs(lat).toFixed(2)}°${latDir} . ${Math.abs(lon).toFixed(2)}°${lonDir}`;
  }

  //  Mini-carte Leaflet

  // Recrée ou creer la carte Leaflet dans #minimap et positionne le marqueur sur la ville affichée
  private initOrUpdateMap(){
    if (!this.dashboard)return;
    const container=document.getElementById('minimap');
    if (!container)return; // le template n'a pas encore rendu le conteneur

    const latt=this.dashboard.latitude;
    const long=this.dashboard.longitude;

    this.map=L.map('minimap',{zoomControl: false,attributionControl :false})
      .setView([latt,long],11);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{
      maxZoom:18,subdomains: ['a', 'b', 'c']
    }).addTo(this.map)
    L.control.zoom({ position: 'bottomright' }).addTo(this.map);

    const icon = L.divIcon({
      html: `<div style="width:14px;height:14px;background:#1F4D3D;border:3px solid #FBF6EA;border-radius:50%;box-shadow:0 2px 8px rgba(31,77,61,.5)"></div>`,
      className: '',
      iconAnchor: [7, 7]
    });
    this.marker = L.marker([latt, long], { icon }).addTo(this.map);
    setTimeout(() => this.map?.invalidateSize(), 150);
  }

  // Détruit proprement l'instance Leaflet avant que son conteneur ne disparaisse du DOM
  private destroyMap(){
    if (this.map){
      try {
        this.map.remove();
      }catch {/* conteneur deja retire , rien a faire */}
        this.map=null;
        this.marker=null;
    }
  }

  protected readonly alert = alert;

}
