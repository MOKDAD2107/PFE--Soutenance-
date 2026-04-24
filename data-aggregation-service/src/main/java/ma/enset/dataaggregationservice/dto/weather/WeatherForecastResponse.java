package ma.enset.dataaggregationservice.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data @AllArgsConstructor @NoArgsConstructor
public class WeatherForecastResponse {
    private Long id;
    private LocalDateTime date;
    private Double predictedTemp;
    private Double predictedHumidity;
    private Double predictedWindSpeed;
    private Double precipitationProbability;
    private String description;
    private String sourceApi;
    private boolean expired;
    private WeatherDataResponse.LocationSummary location;

}
