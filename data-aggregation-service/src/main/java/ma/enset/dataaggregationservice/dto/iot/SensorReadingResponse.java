package ma.enset.dataaggregationservice.dto.iot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class SensorReadingResponse {
    private Long id;
    private Double valeur;
    private String unite;
    private Long sensorId;
    private String status;
    private LocalDateTime readingDate;
}
