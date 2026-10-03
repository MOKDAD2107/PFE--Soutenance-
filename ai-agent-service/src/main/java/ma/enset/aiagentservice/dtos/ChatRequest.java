package ma.enset.aiagentservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class ChatRequest {
    private String message;
    private String cityName;
    private Long locationId;
    private List<Map<String,String>> history;
}
