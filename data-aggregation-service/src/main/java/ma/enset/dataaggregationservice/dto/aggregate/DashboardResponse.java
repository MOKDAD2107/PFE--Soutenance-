package ma.enset.dataaggregationservice.dto.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.dataaggregationservice.dto.iot.EnvironmentAlertResponse;
import ma.enset.dataaggregationservice.dto.iot.IotSensorResponse;
import ma.enset.dataaggregationservice.dto.iot.SensorReadingResponse;
import ma.enset.dataaggregationservice.dto.weather.WeatherDataResponse;
import ma.enset.dataaggregationservice.dto.weather.WeatherForecastResponse;

import java.time.LocalDateTime;
import java.util.List;
@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class DashboardResponse {
    //infos de la ville
    private Long locationId;
    private String cityName;
    private String country;
    private double latitude;
    private double longitude;

    //meteo actuel
    private WeatherDataResponse weatherData;

    // prevision 5 jours
    private List<WeatherForecastResponse> forecastData;

    //capteurs
    private List<IotSensorResponse> sensor;

    //dernieres lectures de chaque capteur
    private List<SensorReadingResponse> reading;

    //alerte active dans cette ville
    private List<EnvironmentAlertResponse> alerts;

    private LocalDateTime generatedAt;

}
