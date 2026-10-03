import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { CitySummary, GlobalCitySummary } from '../../core/models/models';
import { AggregationService } from '../../core/services/AggregationService';
import { DatePipe, DecimalPipe } from '@angular/common';
import * as L from 'leaflet';
import 'leaflet.markercluster'


@Component({
  selector: 'app-map',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements OnInit, AfterViewInit, OnDestroy {
  summary: GlobalCitySummary | null = null;
  selectedCity: CitySummary | null = null;
  loading = true;

  searchText = '';
  showAlertsLayer = true;
  showMeteoLayer = true;

  private map: L.Map | null = null;
  private mapReady = false;

  private meteoClusterGroup: any = (L as any).markerClusterGroup({
    maxClusterRadius: 50,
    spiderfyOnMaxZoom: true,
    showCoverageOnHover: false,
  });

  private meteoMarkersByCity = new globalThis.Map<number, L.Marker>();



  constructor(private aggregation: AggregationService) {}

  ngOnInit(): void {
    this.aggregation.getGlobalSummary().subscribe({
      next: (data) => {
        this.summary = data;
        console.log(this.summary.cities.map(c => ({city: c.city, alerts: c.activeAlertCount})))
        this.loading = false;
        // Le conteneur vient d'apparaître dans le DOM via le @if, on laisse Angular rendre
        if (this.mapReady) {
          this.renderCitiesLayer();
        }
      },
      error: (err) => {
        console.error('Erreur global summary:', err);
        this.loading = false;
      },
    });
  }

  ngAfterViewInit(): void {
      setTimeout(() => this.initMap(), 0);
  }


  ngOnDestroy(): void {
    this.map?.remove();
    this.map = null;
  }

  private initMap(): void {
    if (this.map) return;
    const container = document.getElementById('leaflet-map');
    if (!container) return;

    this.map = L.map('leaflet-map', { zoomControl: true }).setView([31.8, -6.5], 7);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '',
    }).addTo(this.map);
    this.meteoClusterGroup.addTo(this.map)

    this.mapReady = true;

    // Un léger délai + invalidateSize évite les cartes qui restent grises
    setTimeout(() => this.map?.invalidateSize(), 100);

    if (this.summary) {
      this.renderCitiesLayer();
    }

  }

  // ── Filtrage de la liste (barre de recherche) ──
  get filteredCities(): CitySummary[] {
    if (!this.summary) return [];
    const q = this.searchText.toLowerCase().trim();
    if (!q) return this.summary.cities;
    return this.summary.cities.filter((c) => c.city.toLowerCase().includes(q));
  }

  onSearchChange(value: string): void {
    this.searchText = value;
  }

  onSearchEnter(): void {
    const match = this.filteredCities[0];
    if (match) this.flyToCity(match);
  }

  flyToCity(city: CitySummary): void {
    this.selectedCity = city;
    if (!this.map || city.latitude == null || city.longitude == null) return;
    this.map.flyTo([city.latitude, city.longitude], 10, { duration: 1.2 });
    // Le marqueur peut être caché dans un cluster : on zoome dessus avant d'ouvrir le popup
    const marker = this.meteoMarkersByCity.get(city.locationId);
    if (marker) {
      this.meteoClusterGroup.zoomToShowLayer(marker, () => marker.openPopup());
    }
  }

  // ── Calques ──
  toggleAlertsLayer(): void {
    this.showAlertsLayer = !this.showAlertsLayer;
    this.renderCitiesLayer();
  }

  toggleMeteoLayer(): void {
    this.showMeteoLayer = !this.showMeteoLayer;
    if (!this.map) return;
    if (this.showMeteoLayer) {
      this.meteoClusterGroup.addTo(this.map);
    } else {
      this.map.removeLayer(this.meteoClusterGroup);
    }
  }

  // ── Un seul marqueur par ville : badge météo + bordure/pastille d'alerte fusionnés ──
  private renderCitiesLayer(): void {
    if (!this.summary?.cities) return;

    this.meteoClusterGroup.clearLayers();
    this.meteoMarkersByCity.clear();

    this.summary.cities.forEach((city) => {
      if (city.latitude == null || city.longitude == null) return;

      const hasIcon = !!city.weatherIcon;
      const hasAlert = this.showAlertsLayer && city.activeAlertCount > 0;
      const borderColor = hasAlert ? '#A3372B' : '#FBF6EA';

      const icon = L.divIcon({
        className: '',
        html: `
          <div style="position:relative;width:46px;height:46px;">
            <div style="width:46px;height:46px;border-radius:50%;background:#2C5F7C;
                display:flex;align-items:center;justify-content:center;
                border:3px solid ${borderColor};box-shadow:0 3px 10px rgba(0,0,0,.3);">
              ${
          hasIcon
            ? `<img src="https://openweathermap.org/img/wn/${city.weatherIcon}@2x.png"
                       alt="${city.weatherDescription || 'météo'}" style="width:34px;height:34px;" />`
            : `<span style="color:#FBF6EA;font-size:11px;font-family:'IBM Plex Mono',monospace;font-weight:600;">
                       ${city.temperature != null ? Math.round(city.temperature) + '°' : '?'}
                     </span>`
        }
            </div>
            ${
          hasAlert
            ? `<div style="position:absolute;top:-4px;right:-4px;width:17px;height:17px;border-radius:50%;
                      background:#A3372B;border:2px solid #FBF6EA;display:flex;align-items:center;justify-content:center;
                      color:#FBF6EA;font-size:9px;font-weight:700;font-family:'IBM Plex Mono',monospace;">
                      ${city.activeAlertCount}
                   </div>`
            : ''
        }
          </div>`,
        iconSize: [46, 46],
        iconAnchor: [23, 23],
      });

      const popup = `
        <div style="font-family:'Inter',sans-serif;min-width:180px;">
          <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px;">
            ${hasIcon ? `<img src="https://openweathermap.org/img/wn/${city.weatherIcon}@2x.png" alt="${city.weatherDescription || 'météo'}" width="40" height="40"/>` : ''}
            <div style="font-family:'Fraunces',serif;font-size:16px;font-weight:600;">${city.city}</div>
          </div>
          <div>🌡 ${city.temperature != null ? Math.round(city.temperature) + '°C' : 'N/A'}</div>
          <div>💧 ${city.humidity != null ? city.humidity + '%' : 'N/A'}</div>
          <div>💨 ${city.windSpeed != null ? city.windSpeed + ' m/s' : 'N/A'}</div>
          <div>📡 ${city.activeSensorsCount} capteur(s) actif(s)</div>
          ${city.activeAlertCount > 0 ? `<div style="color:#A3372B;margin-top:6px;font-weight:600;">⚠ ${city.activeAlertCount} alerte(s)</div>` : ''}
        </div>
      `;

      const marker = L.marker([city.latitude, city.longitude], { icon });
      marker.bindTooltip(city.city, { direction: 'top', offset: [0, -20] });
      marker.bindPopup(popup);
      marker.on('click', () => (this.selectedCity = city));

      this.meteoClusterGroup.addLayer(marker);
      this.meteoMarkersByCity.set(city.locationId, marker);
    });
  }

}
