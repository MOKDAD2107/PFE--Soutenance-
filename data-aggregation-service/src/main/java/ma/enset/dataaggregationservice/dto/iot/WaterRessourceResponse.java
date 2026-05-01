package ma.enset.dataaggregationservice.dto.iot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.dataaggregationservice.dto.weather.LocationResponse;


import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaterRessourceResponse {
    private Long id;
    private String name;
    private String ressourceType;
    private Double capaciteMax;
    private Double currentLevel;
    private Double fillPercentage;
    private String fillStatus;
    private LocalDateTime lastUpdate;
    private LocationResponse location;
}
