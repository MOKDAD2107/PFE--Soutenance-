package ma.enset.aiagentservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaterStatusResponse {
    private List<WaterRessourceResponse> barrages;
    private List<WaterRessourceResponse> riviere;
    private List<WaterRessourceResponse> lacs;
    private List<WaterRessourceResponse> nappe;

    private Map<String, Long> barrageStats;
    private Map<String, Long> riviereStats;
    private Map<String, Long> lacsStats;
    private Map<String, Long> nappeStats;

    private int totalBarrages;
    private int totalRiveries;
    private int totalLacs;
    private int totalNappe;

    private LocalDateTime generatedAt;

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WaterRessourceResponse {
        private Long id;
        private String name;
        private String ressourceType;
        private Double capaciteMax;
        private Double currentLevel;
        private Double fillPercentage;
        private String fillStatus;
        private LocationInfo location;
    }
    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationInfo{
        private Long id;
        private String nameCity;
        private String country;
        private String region;
    }
}
