package ma.enset.aiagentservice.controller;

import ma.enset.aiagentservice.agent.AiAgent;

import ma.enset.aiagentservice.dtos.ChatRequest;
import ma.enset.aiagentservice.dtos.ChatResponse;
import ma.enset.aiagentservice.dtos.ReportRequest;
import ma.enset.aiagentservice.service.AiAgentService;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class ChatController {
    private final AiAgent aiAgent;
    private final AiAgentService aiAgentService;
    public ChatController(AiAgent aiAgent, AiAgentService aiAgentService) {
        this.aiAgent = aiAgent;
        this.aiAgentService = aiAgentService;
    }
    @GetMapping(value = "/chatResponse/{query}",produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@PathVariable String query) {
        return aiAgent.askAgent(new Prompt(query));
    }
    @PostMapping(value = "/chat",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponse> chatResponse(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(aiAgentService.chat(request));
    }
    @GetMapping(value = "/summary/{locationId}",produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> summary(@PathVariable Long locationId) {
        return ResponseEntity.ok(aiAgentService.generateSummary(locationId));
    }
    @GetMapping(value = "/alert/{alertType},{message},{severity}",produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> explainAlerts(@PathVariable String alertType,@PathVariable String message,@PathVariable String severity) {
        return ResponseEntity.ok(aiAgentService.explainAlert(alertType,message,severity));
    }
    @GetMapping(value = "/rapport/{request}",produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> rapport(@PathVariable ReportRequest request) {
        return ResponseEntity.ok(aiAgentService.generateReport(request));
    }
    @GetMapping(value = "/compare/{id1},{id2}",produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> compareCities(@PathVariable Long id1,@PathVariable Long id2) {
        return ResponseEntity.ok(aiAgentService.compareCity(id1,id2));
    }
}
