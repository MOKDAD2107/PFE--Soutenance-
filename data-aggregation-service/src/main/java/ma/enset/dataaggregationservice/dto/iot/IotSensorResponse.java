package ma.enset.dataaggregationservice.dto.iot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.dataaggregationservice.dto.weather.LocationResponse;


import java.time.LocalDateTime;
@Data @NoArgsConstructor
@AllArgsConstructor @Builder
public class IotSensorResponse {
    private Long id;
    private String name;
    private String sensorType;
    private LocationResponse location;
    private String description;
    private String unite;
    private boolean active;
    private LocalDateTime lastReadingDate;
}
