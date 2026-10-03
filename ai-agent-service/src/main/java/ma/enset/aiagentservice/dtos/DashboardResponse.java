package ma.enset.aiagentservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardResponse {
    private Long locationId;
    @JsonProperty("city")
    private String cityName;
    private WeatherData weatherData;
    @JsonProperty("forecastData")
    private List<WeatherForecast> weatherForecasts;
    @JsonProperty("reading")
    private List<SensorReading> sensorReadings;
    private List<Alert> alerts;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherData{
        private Double temperature;
        private Double humidity;
        private Double pressure;
        private Double windSpeed;
        private String description;
        private String sourceApi;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherForecast{
        private LocalDateTime date;
        private Double predictedTemp;
        private Double predictedHumidity;
        private Double predictedWindSpeed;
        private Double precipitationProbability;
        private String description;
        private String sourceApi;
        private boolean expired;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SensorReading{
        private Double valeur;
        private String unite;
        private Long sensorId;
        private String status;
        private LocalDateTime readingDate;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Alert{
        private String alertType;
        private String message;
        private String severity;
        private String status;
        private Long locationId;
        private Long sensorId;

    }
}
