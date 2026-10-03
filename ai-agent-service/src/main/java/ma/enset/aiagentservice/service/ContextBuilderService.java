package ma.enset.aiagentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.aiagentservice.dtos.DashboardResponse;
import ma.enset.aiagentservice.dtos.GlobalSummaryResponse;
import ma.enset.aiagentservice.feign.AggregationClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j

public class ContextBuilderService {
    @Autowired
    private AggregationClient  aggregationClient;

    public String buildContext(String cityName, Long locationId) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("Données ECOWATCH en temps réel\n\n");
        // Données spécifiques pour chaque une ville
        if (locationId != null) {
            try {
                DashboardResponse dashboard = aggregationClient.getDashboard(locationId);
                ctx.append("Ville : ").append(dashboard.getCityName()).append("\n");
                if (dashboard.getWeatherData() != null) {
                    DashboardResponse.WeatherData weatherData = dashboard.getWeatherData();
                    ctx.append("Météo Actuelle : \n");
                    ctx.append("Temperature:").append(weatherData.getTemperature()).append("°C\n");
                    ctx.append("Description:").append(weatherData.getDescription()).append("\n");
                    ctx.append("Humidité:").append(weatherData.getHumidity()).append("%\n");
                    ctx.append("Vent:").append(weatherData.getWindSpeed()).append("m/s\n");
                    ctx.append("Pression:").append(weatherData.getPressure()).append("hPa\n");
                }
                if (dashboard.getWeatherForecasts() != null&&!dashboard.getWeatherForecasts().isEmpty()) {
                    ctx.append("Prévision des cinq jours:\n");
                    dashboard.getWeatherForecasts().stream()
                            .limit(5).forEach(fc->
                                    ctx.append(" ").append(fc.getDate()!=null ?fc.getDate():"?")
                                            .append(" : ").append(fc.getPredictedTemp()).append("°C")
                                            .append(" , ").append(fc.getDescription())
                                            .append(",pluie").append(fc.getPredictedHumidity()).append("%\n")
                            );
                }
                if (dashboard.getAlerts()!=null&&!dashboard.getAlerts().isEmpty()) {
                    ctx.append("\nLes alertes actives:").append(dashboard.getAlerts().size()).append("\n");
                    dashboard.getAlerts().forEach(al->
                            ctx.append("Sévérité ").append(al.getSeverity()).append(", ")
                                    .append(al.getAlertType()).append(" : ").append(al.getMessage()).append("\n")
                    );
                }else{
                    ctx.append("\nAlertes: aucune alerte active.\n");
                }
                if (dashboard.getSensorReadings() != null && !dashboard.getSensorReadings().isEmpty()) {
                    ctx.append("\nCAPTEURS IoT :\n");
                    dashboard.getSensorReadings().forEach(r ->
                            ctx.append("  Capteur #").append(r.getSensorId())
                                    .append(" : ").append(r.getValeur())
                                    .append(" ").append(r.getUnite())
                                    .append(" → ").append(r.getStatus()).append("\n")
                    );
                }
            }catch (Exception e){
                log.warn("Impossible de récupérer les données de la ville {}: {}", locationId, e.getMessage());
                ctx.append("[Données ville indisponibles]\n");
            }
        }
        // Résumé global Maroc
        try {
            GlobalSummaryResponse global = aggregationClient.getGlobalSummary();
            ctx.append("\nRésumé global Maroc :\n");
            ctx.append("  Alertes actives totales : ").append(global.getTotalActiveAlerts()).append("\n");
            ctx.append("  Capteurs actifs         : ").append(global.getTotalActiveSensors()).append("\n");
            ctx.append("  Les ressources d'eau critiques      : ").append(global.getTotalCriticalWaterRessources()).append("\n");

            if (global.getCities() != null && !global.getCities().isEmpty()) {
                ctx.append("\n  RÉSUMÉ PAR VILLE :\n");
                global.getCities().forEach(c ->
                        ctx.append("  - ").append(c.getCityName())
                                .append(" : ").append(c.getTemperature()).append("°C")
                                .append(", ").append(c.getWeatherDescription())
                                .append(", alertes: ").append(c.getActiveAlertCount()).append("\n")
                );
            }
        } catch (Exception e){
            log.warn("Impossible de récupérer le résumé global: {}", e.getMessage());
            ctx.append("[Résumé global indisponible]\n");
        }
        ctx.append("\nFin des données.\n");
        return ctx.toString();
    }
    //Contexte minimal pour les qts générales sans ville spécifique
    public String buildGlobalContext() {
        return buildContext(null, null);
    }
}
