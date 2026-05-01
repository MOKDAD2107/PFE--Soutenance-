package ma.enset.dataaggregationservice.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponse {
    private Long id;
    private String nameCity;
    private String country;
    private String region;
    private Double latitude;
    private Double longitude;
}
