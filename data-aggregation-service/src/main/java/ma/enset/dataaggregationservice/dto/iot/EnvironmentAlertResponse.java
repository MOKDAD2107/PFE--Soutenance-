package ma.enset.dataaggregationservice.dto.iot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.dataaggregationservice.dto.weather.LocationResponse;


import java.time.LocalDateTime;
@Data @NoArgsConstructor
@AllArgsConstructor @Builder
public class EnvironmentAlertResponse {
    private Long id;
    private String alertType;
    private String message;
    private String severity;
    private String status;
    private LocationResponse location;
    private Long sensorId;
    private Double triggerValue;
    private Double seuilDepasse;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
}
