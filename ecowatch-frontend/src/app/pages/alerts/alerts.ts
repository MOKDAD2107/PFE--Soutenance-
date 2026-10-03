import {Component, OnDestroy, OnInit} from '@angular/core';
import {AlertSeverity, AlertStatus, EnvironmentAlert} from '../../core/models/models';
import {DatePipe, NgClass} from '@angular/common';
import {interval, Subscription} from 'rxjs';
import {AuthService} from '../../core/services/AuthService';
import {AlertService} from '../../core/services/AlertService';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';

type Domaine='all'|'eau'|'meteo'|'air'|'capteurs';
type StatusFilter = 'all' | AlertSeverity | 'RESOLVED' | 'IGNORED';
type TriMode = 'recent' | 'severity' | 'ville';

const DomaineLabel:Record<Exclude<Domaine, 'all'>, string>={
  eau:'Eau',
  meteo:'Meteo',
  air:'AirQuality',
  capteurs:'Capteurs'
}
const SeverityOrder:Record<AlertSeverity, number>={
  CRITICAL:0,
  LOW:1,
  MEDIUM:2,
  HIGH:3
}
@Component({
  selector: 'app-alerts',
  imports: [
    DatePipe,
    FormsModule,
    NgClass
  ],
  templateUrl: './alerts.html',
  styleUrl: './alerts.css',
})
export class Alerts implements OnInit,OnDestroy{
  allAlerts: EnvironmentAlert[] = [];
  loading = true;

  activeDomaine:Domaine='all';
  activeFilter:StatusFilter='all';
  searchText='';
  triMode:TriMode='recent';

  readonly isAdmin: boolean;
  domainLabels = DomaineLabel;
  domainKeys: Exclude<Domaine, 'all'>[] = ['eau', 'air', 'meteo', 'capteurs'];

  // ── résoudre / ignorer ──
  actionTarget: EnvironmentAlert | null = null;
  actionType: 'resolve' | 'ignore' | null = null;
  actionNote = '';
  actionSaving = false;
  actionError = '';

  // ── auto-refresh ──
  private pollSub?: Subscription;
  newAlertsCount = 0;
  private knownIds = new Set<number>();

  constructor(private alertService: AlertService,
              private authService:AuthService,
              private router:Router
  ) {
    this.isAdmin = this.authService.hasRole('ADMIN');
  }

  ngOnInit(): void {
    this.load(true);
    this.pollSub = interval(60000).subscribe(() => this.load(false));
  }

  ngOnDestroy(): void {
    this.pollSub?.unsubscribe();
  }
  load(initial: boolean): void {
    this.alertService.getAll().subscribe({
      next: (data) => {
        if (!initial) {
          const nouvelles = data.filter(a => a.status === 'ACTIVE' && !this.knownIds.has(a.id)).length;
          this.newAlertsCount += nouvelles;
        }
        this.allAlerts = data;
        data.forEach(a => this.knownIds.add(a.id));
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement alertes:', err);
        this.loading = false;
      }
    });
  }
  dismissNewBadge(): void {
    this.newAlertsCount = 0;
    this.load(true);
  }

  // ---------- domaine ----------
  private domaineOf(alertType: string): Exclude<Domaine, 'all'> {
    if (alertType.startsWith('WATER_LEVEL')) return 'eau';
    if (alertType.startsWith('AIR_QUALITY') || alertType.startsWith('POUSSIERE') || alertType.startsWith('DUST')) return 'air';
    if (alertType.startsWith('TEMPERATURE')) return 'meteo';
    return 'capteurs'; // CO2, HUMIDITY_SOL...
  }

  domainCounts(): Record<string, number> {
    const c: Record<string, number> = {all: this.allAlerts.length};
    this.domainKeys.forEach(d => c[d] = this.allAlerts.filter(a => this.domaineOf(a.alertType) === d).length);
    return c;
  }

  setDomain(d: Domaine): void {
    this.activeDomaine = d;
  }

  // ---------- filtre sévérité / statut ----------
  setFilter(f: StatusFilter): void {
    this.activeFilter = f;
  }

  private matchesFilter(a: EnvironmentAlert): boolean {
    if (this.activeFilter === 'all') return true;
    if (this.activeFilter === 'RESOLVED') return a.status === 'RESOLVED';
    if (this.activeFilter === 'IGNORED') return a.status === 'IGNORED';
    return a.severity === this.activeFilter;
  }
  private matchesSearch(a: EnvironmentAlert): boolean {
    const q = this.searchText.trim().toLowerCase();
    if (!q) return true;
    return a.alertType.toLowerCase().includes(q)
      || a.message.toLowerCase().includes(q)
      || (a.location?.nameCity || '').toLowerCase().includes(q);
  }

  onSearchChange(v: string): void {
    this.searchText = v;
  }

  onSortChange(v: string): void {
    this.triMode = v as TriMode;
  }

  get filteredAlerts(): EnvironmentAlert[] {
    const base = this.allAlerts.filter(a =>
      (this.activeDomaine === 'all' || this.domaineOf(a.alertType) === this.activeDomaine)
      && this.matchesFilter(a)
      && this.matchesSearch(a)
    );
    return this.sortAlerts(base);
  }
  private sortAlerts(list: EnvironmentAlert[]): EnvironmentAlert[] {
    if (this.triMode === 'severity') {
      return [...list].sort((a, b) =>
        SeverityOrder[a.severity] - SeverityOrder[b.severity]
        || +new Date(b.triggeredAt) - +new Date(a.triggeredAt));
    }
    if (this.triMode === 'ville') {
      return [...list].sort((a, b) => (a.location?.nameCity || '').localeCompare(b.location?.nameCity || ''));
    }
    return [...list].sort((a, b) => +new Date(b.triggeredAt) - +new Date(a.triggeredAt));
  }

  // ---------- tuiles ----------
  get tileCritical(): number { return this.countActive('CRITICAL'); }
  get tileHigh(): number { return this.countActive('HIGH'); }
  get tileMedium(): number { return this.countActive('MEDIUM'); }
  get tileLow(): number { return this.countActive('LOW'); }
  get tileResolved(): number { return this.allAlerts.filter(a => a.status === 'RESOLVED').length; }
  get tileIgnored(): number { return this.allAlerts.filter(a => a.status === 'IGNORED').length; }

  private countActive(sev: AlertSeverity): number {
    return this.allAlerts.filter(a => a.status === 'ACTIVE' && a.severity === sev).length;
  }

  // ---------- navigation contextuelle ----------
  goToSource(a: EnvironmentAlert): void {
    const domain = this.domaineOf(a.alertType);
    if (domain === 'eau') {
      // sensorId = id de la WaterRessource pour ce type d'alerte
      this.router.navigate(['/water-status'], {queryParams: {highlight: a.sensorId}});
      return;
    }
    if (a.location) {
      this.router.navigate(['/dashboard', a.location.id]);
    }
  }

  // ---------- résoudre / ignorer (ADMIN) ----------
  openAction(a: EnvironmentAlert, type: 'resolve' | 'ignore'): void {
    this.actionTarget = a;
    this.actionType = type;
    this.actionNote = '';
    this.actionError = '';
  }

  closeAction(): void {
    this.actionTarget = null;
    this.actionType = null;
    this.actionNote = '';
  }

  confirmAction(): void {
    if (!this.actionTarget || !this.actionType) return;
    this.actionSaving = true;
    this.actionError = '';
    const call = this.actionType === 'resolve'
      ? this.alertService.resolve(this.actionTarget.id, this.actionNote || undefined)
      : this.alertService.ignore(this.actionTarget.id, this.actionNote || undefined);
    call.subscribe({
      next: () => {
        this.actionSaving = false;
        this.closeAction();
        this.load(true);
      },
      error: (err) => {
        this.actionSaving = false;
        this.actionError = err.status === 403
          ? 'Action réservée aux administrateurs.'
          : "Erreur lors de l'action. Réessaie.";
        console.error('Erreur action alerte:', err);
      }
    });
  }

  // ---------- affichage ----------
  severityLabel(s: AlertSeverity): string {
    switch (s) {
      case 'CRITICAL': return 'Critique';
      case 'HIGH': return 'Élevée';
      case 'MEDIUM': return 'Moyenne';
      case 'LOW': return 'Faible';
      default: return s;
    }
  }

  statusLabel(s: AlertStatus): string {
    switch (s) {
      case 'ACTIVE': return 'Active';
      case 'RESOLVED': return 'Résolue';
      case 'IGNORED': return 'Ignorée';
      default: return s;
    }
  }
  typeLabel(alertType: string): string {
    return alertType.replace(/_/g, ' ');
  }

}
