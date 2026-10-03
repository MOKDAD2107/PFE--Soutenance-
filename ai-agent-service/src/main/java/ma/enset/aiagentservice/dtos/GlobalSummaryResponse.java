package ma.enset.aiagentservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor
@AllArgsConstructor @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalSummaryResponse {
    private int totalActiveAlerts;
    private int totalActiveSensors;
    @JsonProperty("totalCriticalWaterResources")
    private int totalCriticalWaterRessources;
    private List<citySummary> cities;
    @JsonProperty("allActivesAlerts")
    private List<alert> alerts;
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class citySummary{
        private Long locationId;
        @JsonProperty("city")
        private String cityName;
        private Double temperature;
        private String weatherDescription;
        private int activeAlertCount;
        @JsonProperty("activeSensorsCount")
        private int activeSensorCount;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class alert{
        private String alertType;
        private String message;
        private String severity;
    }
}
