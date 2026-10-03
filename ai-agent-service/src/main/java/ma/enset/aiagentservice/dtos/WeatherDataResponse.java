package ma.enset.aiagentservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data @AllArgsConstructor @NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDataResponse {
    private Long id;
    private LocalDateTime dateTime;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private Double pressure;
    private Double uvIndex;
    private String description;
    private String sourceApi;
    private LocationInfo location;
    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationInfo{
        private Long id;
        private String nameCity;
        private String country;
        private Double latitude;
        private Double longitude;
    }
}
