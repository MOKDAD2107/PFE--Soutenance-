package ma.enset.dataaggregationservice.dto.iot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.dataaggregationservice.dto.weather.LocationResponse;


import java.time.LocalDateTime;
@Data @NoArgsConstructor
@AllArgsConstructor @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class IotSensorResponse {
    private Long id;
    private String name;
    private String sensorType;
    private Long locationId;
    private LocationResponse location;
    private String description;
    private String unite;
    private boolean active;
    private LocalDateTime lastReadingDate;
}
