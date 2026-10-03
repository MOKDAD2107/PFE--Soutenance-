package ma.enset.dataaggregationservice.dto.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.dataaggregationservice.dto.iot.EnvironmentAlertResponse;
import ma.enset.dataaggregationservice.dto.iot.IotSensorResponse;
import ma.enset.dataaggregationservice.dto.iot.WaterRessourceResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class GlobalSummaryResponse {
    //un resume par ville
    private List<CitySummary> cities;
    // toutes les alertes actives
    private List<EnvironmentAlertResponse>  allActivesAlerts;
    // Barrages en situation critique
    private List<WaterRessourceResponse> criticalWaterResources;
    private List<IotSensorResponse> activeSensor;

    // Statistiques globales
    private int totalActiveSensors;
    private int totalActiveAlerts;
    private int totalCriticalWaterResources;
    private Double averageTemperature;
    private Double averageAirQuality;
    private Double averageDust;

    private LocalDateTime generatedAt;

    //resumer d'une ville
    @Data @AllArgsConstructor @NoArgsConstructor
    @Builder
    public static class CitySummary{
    private Long locationId;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;

    //meteo
    private double temperature;
    private double humidity;;
    private double windSpeed;
    private String weatherDescription;
    private String weatherIcon;

    //capteur
  //  private String airQualityStatus; // NORMAL-WARNING-DANGER
    //statistiques
    private int activeSensorsCount;
    private int activeAlertCount;
    private int criticalWaterRessource;

    }
}
