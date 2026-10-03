package ma.enset.aiagentservice.tools;

import ma.enset.aiagentservice.dtos.DashboardResponse;
import ma.enset.aiagentservice.dtos.GlobalSummaryResponse;
import ma.enset.aiagentservice.dtos.WaterStatusResponse;
import ma.enset.aiagentservice.dtos.WeatherDataResponse;
import ma.enset.aiagentservice.feign.AggregationClient;
import ma.enset.aiagentservice.feign.WeatherClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class EcowatchTools {
    private final AggregationClient aggregationClient;
    private final WeatherClient weatherClient;

    public EcowatchTools(AggregationClient aggregationClient, WeatherClient weatherClient) {
        this.aggregationClient = aggregationClient;
        this.weatherClient = weatherClient;
    }
    @Tool(name = "getWeatherAndAlerts", description = "Récupère la météo actuelle et les alertes d'une ville par son ID" +
            "Utilise cet outil quand l'utilisateur pose une question sur une ville spécifique")
    public String getWeatherAndAlert(@ToolParam(description = "ID de la ville (ex: 1=Casablanca, 2=Rabat)") Long locationId) {
        try {
            DashboardResponse dashboard = aggregationClient.getDashboard(locationId);
            StringBuilder sb = new StringBuilder();
            sb.append("Ville : ").append(dashboard.getCityName()).append("\n");
            if (dashboard.getWeatherData() != null) {
                sb.append("Temperature:").append(dashboard.getWeatherData().getTemperature()).append("°C\n");
                sb.append("Description:").append(dashboard.getWeatherData().getDescription()).append("\n");
                sb.append("Humidité:").append(dashboard.getWeatherData().getHumidity()).append("%\n");
                sb.append("Vent:").append(dashboard.getWeatherData().getWindSpeed()).append("m/s\n");
                sb.append("Pression:").append(dashboard.getWeatherData().getPressure()).append("hPa\n");
            }
            if (dashboard.getSensorReadings() != null && !dashboard.getSensorReadings().isEmpty()) {
                sb.append("\nCapteurs IoT :\n");
                dashboard.getSensorReadings().forEach(r ->
                        sb.append("  - Capteur ").append(r.getSensorId())
                                .append(" : ").append(r.getValeur())
                                .append(" ").append(r.getUnite())
                                .append(", statut ").append(r.getStatus()).append("\n")
                );
            }
            if (dashboard.getAlerts() != null && !dashboard.getAlerts().isEmpty()) {
                sb.append("\nAlertes actives (").append(dashboard.getAlerts().size()).append(") :\n");
                dashboard.getAlerts().forEach(a -> sb.append("  - Sévérité ").append(a.getSeverity()).append(", ").append(a.getAlertType())
                        .append(": ").append(a.getMessage()).append("\n"));
            } else {
                sb.append("\nAucune alerte active.\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Données indisponibles pour locationId=" + locationId;
        }
    }

    @Tool(name = "getGlobalSummary", description = "Récupère le résumé global de toutes les villes du Maroc" +
            "nombre total d'alertes, capteurs actifs, les ressources d'eau critiques(barrages, lacs, rivières,nappes phréatiques), et résumé par ville. " +
            "Utilise cet outil pour les questions générales sur le Maroc ou sans ville précise.")
    public String getGlobalSummary() {
        try {
            GlobalSummaryResponse summary = aggregationClient.getGlobalSummary();
            StringBuilder sb = new StringBuilder("Résumé global Maroc :\n");
            sb.append("Les alertes actives:").append(summary.getTotalActiveAlerts()).append("\n");
            sb.append("Les capteurs actifs:").append(summary.getTotalActiveSensors()).append("\n");
            sb.append("Les ressources d'eau critiques:").append(summary.getTotalCriticalWaterRessources()).append("\n");
            if (summary.getCities() != null && !summary.getCities().isEmpty()) {
                sb.append("\nVilles :\n");
                summary.getCities().forEach(c -> sb.append("  - ").append(c.getCityName())
                        .append(":").append(c.getTemperature()).append("°C")
                        .append(",").append(c.getWeatherDescription())
                        .append(",alertes:").append(c.getActiveAlertCount()).append("\n"));
            }
            if (summary.getAlerts() != null && !summary.getAlerts().isEmpty()) {
                sb.append("\nDétail des alertes actives :\n");
                summary.getAlerts().forEach(a -> sb.append("  - Sévérité ").append(a.getSeverity()).append(", ")
                        .append(a.getAlertType()).append(" : ").append(a.getMessage()).append("\n"));
            }
            return sb.toString();
        } catch (Exception e) {
            return "Resume globale indisponible.";
        }
    }

    @Tool(name = "getForecast", description = "Récupère les prévisions météo du 5 jours d'une ville" +
            "Utilise cet outil pour répondre aux questions sur la météo future ou la semaine à venir.")
    public String getForecast(@ToolParam(description = "ID de la ville") Long locationId) {
        try {
            DashboardResponse dashboard = aggregationClient.getDashboard(locationId);
            if (dashboard.getWeatherForecasts() == null || dashboard.getWeatherForecasts().isEmpty()) {
                return "Aucune prevision disponible";
            }
            StringBuilder sb = new StringBuilder("Prevision des 5 jours :\n");
            dashboard.getWeatherForecasts().stream().limit(5).forEach(fc ->
                    sb.append("-").append((fc.getDate() != null) ? fc.getDate() : "?")
                            .append(":").append(fc.getPredictedTemp()).append("°C")
                            .append(", ").append(fc.getDescription())
                            .append(", pluie ").append(fc.getPrecipitationProbability()).append("%\n")
            );
            return sb.toString();
        } catch (Exception e) {
            return "Prevision indisponible pour locationId=." + locationId;
        }
    }

    @Tool(name = "getWeatherByCityName", description = "Récupère la météo actuelle d'une ville par son nom " +
            "(utile pour les villes sans capteur IoT, comme Zagora ou Berkane). " +
            "Utilise cet outil quand l'utilisateur mentionne un nom de ville et que getWeatherAndAlerts échoue ou ne connaît pas l'ID.")
    public String getWeatherByCityName(@ToolParam(description = "Nom de la ville (ex: Zagora, Berkane, Casablanca)") String cityName) {
        try {
            WeatherDataResponse weather = weatherClient.findByCityName(cityName);
            StringBuilder sb = new StringBuilder();
            sb.append("Ville : ").append(weather.getLocation().getNameCity()).append("\n");
            sb.append("Temperature:").append(weather.getTemperature()).append("°C\n");
            sb.append("Description:").append(weather.getDescription()).append("\n");
            sb.append("Humidité:").append(weather.getHumidity()).append("%\n");
            sb.append("Vent:").append(weather.getWindSpeed()).append("m/s\n");
            sb.append("Pression:").append(weather.getPressure()).append("hPa\n");
            return sb.toString();
        } catch (Exception e) {
            return "Aucune donnée météo trouvée pour la ville : " + cityName;
        }
    }
    @Tool(name = "getWaterStatus", description = "Récupère l'état national des ressources en eau du Maroc "+
            "(barrages, lacs, rivières, nappes phréatiques), toutes villes confondues. "+
            "Utilise cet outil pour un bilan national ou quand tu ne connais pas l'ID de la ville demandée.")
    public String getWaterStatus(){
        return buildWaterStatusText(aggregationClient.getWaterStatus(null));
    }
    @Tool(name = "getWaterStatusByCity", description = "Récupère l'état des ressources en eau pour une ville précise "+
            "via son ID de localisation. Utilise cet outil uniquement si tu connais l'ID exact de la ville.")
    public String getWaterStatusByCity(@ToolParam(description = "ID de la ville (ex: 1=Casablanca, 2=Rabat, 4=Marrakech)") Long locationId){
        return buildWaterStatusText(aggregationClient.getWaterStatus(locationId));
    }
    private String buildWaterStatusText(WaterStatusResponse status){
        try {
            StringBuilder sb = new StringBuilder("État des ressources en eau :\n");

            sb.append("\nBarrages (").append(status.getTotalBarrages()).append(") :\n");
            status.getBarrages().forEach(b -> sb.append("  - ").append(b.getName())
                    .append(b.getLocation() != null ? " (" + b.getLocation().getNameCity() + ")" : "")
                    .append(" : ").append(b.getFillPercentage()).append("%, statut ").append(b.getFillStatus()).append("\n"));

            sb.append("\nLacs (").append(status.getTotalLacs()).append(") :\n");
            status.getLacs().forEach(l -> sb.append("  - ").append(l.getName())
                    .append(l.getLocation() != null ? " (" + l.getLocation().getNameCity() + ")" : "")
                    .append(" : ").append(l.getFillPercentage()).append("%, statut ").append(l.getFillStatus()).append("\n"));

            sb.append("\nRivières (").append(status.getTotalRiveries()).append(") :\n");
            status.getRiviere().forEach(r -> sb.append("  - ").append(r.getName())
                    .append(r.getLocation() != null ? " (" + r.getLocation().getNameCity() + ")" : "")
                    .append(" : ").append(r.getFillPercentage()).append("%, statut ").append(r.getFillStatus()).append("\n"));

            sb.append("\nNappes phréatiques (").append(status.getTotalNappe()).append(") :\n");
            status.getNappe().forEach(n -> sb.append("  - ").append(n.getName())
                    .append(n.getLocation() != null ? " (" + n.getLocation().getNameCity() + ")" : "")
                    .append(" : ").append(n.getFillPercentage()).append("%, statut ").append(n.getFillStatus()).append("\n"));


            return sb.toString();
        } catch (Exception e){
            return "État des ressources en eau indisponible.";
        }
    }
}