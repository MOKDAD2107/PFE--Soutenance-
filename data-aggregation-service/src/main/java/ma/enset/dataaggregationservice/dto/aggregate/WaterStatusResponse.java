package ma.enset.dataaggregationservice.dto.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.dataaggregationservice.dto.iot.WaterRessourceResponse;

import java.time.LocalDateTime;
import java.util.List;
@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class WaterStatusResponse {
    private List<WaterRessourceResponse> barrages;
    private List<WaterRessourceResponse> riviere;
    private List<WaterRessourceResponse> lacs;
    private List<WaterRessourceResponse> nappe;

    //Statistiques des barrages
    private int totalBarrages;
    private int totalRiveries;
    private int totalLacs;
    private int totalNappe;
    private int barragesCritiques;
    private int barragesBas;
    private int barragesNormaux;
    private int barragesEleves;
    private double fillPercentage;
    private LocalDateTime generatedAt;

}
