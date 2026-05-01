package ma.enset.dataaggregationservice.dto.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.dataaggregationservice.dto.iot.WaterRessourceResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class WaterStatusResponse {
    private List<WaterRessourceResponse> barrages;
    private List<WaterRessourceResponse> riviere;
    private List<WaterRessourceResponse> lacs;
    private List<WaterRessourceResponse> nappe;

    private Map<String,Long> barrageStats;
    private Map<String,Long> nappeStats;
    private Map<String,Long> lacsStats;
    private Map<String,Long> riviereStats;

    private int totalBarrages;
    private int totalLacs;
    private int totalRiveries;
    private int totalNappe;

    private LocalDateTime generatedAt;

}
