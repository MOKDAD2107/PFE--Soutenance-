package ma.enset.dataaggregationservice.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data @AllArgsConstructor
@NoArgsConstructor
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
    private LocationSummary location;

    @Data
    public static class LocationSummary{
            private Long id;
            private String nameCity;
            private String country;
            private Double latitude;
            private Double longitude;
    }


}
