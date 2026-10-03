package ma.enset.aiagentservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ReportRequest {
    private String cityName;
    private Long locationId;
    private String reportType; // DAILY , WEEKLY , WATER , AIR

}
