package ma.enset.aiagentservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ChatResponse {
    private String reply;
    private String cityName;
    private LocalDateTime timestamp;
    private String contextUsed;
}
