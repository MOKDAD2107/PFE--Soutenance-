package ma.enset.dataaggregationservice.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {
    private Long id;
    private String nameCity;
    private String country;
    private String region;
    private Double latitude;
    private Double longitude;
}
