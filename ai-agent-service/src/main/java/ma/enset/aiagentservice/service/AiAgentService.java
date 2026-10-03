package ma.enset.aiagentservice.service;

import ma.enset.aiagentservice.agent.AiAgent;
import ma.enset.aiagentservice.dtos.ChatRequest;
import ma.enset.aiagentservice.dtos.ChatResponse;
import ma.enset.aiagentservice.dtos.ReportRequest;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class AiAgentService {
    @Autowired
    private AiAgent aiAgent;
    @Autowired
    private ContextBuilderService builderService;
    public ChatResponse chat(ChatRequest request){
        String context=(request.getLocationId()!=null)
                ?builderService.buildContext(request.getCityName(),request.getLocationId())
                :builderService.buildGlobalContext();
        // injecter le contexte dans le msg d'utilisateur:
        String msg="Contexte ECOWATCH actuelle:\n"+context+"\nQuestion:"+request.getMessage();
        String response=aiAgent.askAgent(new Prompt(msg));
        return ChatResponse.builder()
                .reply(response)
                .cityName(request.getCityName())
                .timestamp(LocalDateTime.now()).build();
    }
    // Résumé du dashboard
    public String generateSummary(Long locationId) {
        String context = builderService.buildContext(null, locationId);
        String prompt = "Contexte EcoWatch :\n" + context
                + "\n\nGénère un résumé bref (3-4 phrases) de la situation environnementale. "
                + "Mentionne météo, alertes, qualité de l'air. Commence directement.";
        return aiAgent.askAgent(new Prompt(prompt));
    }

    // Explication des alertes
    public String explainAlert(String alertType, String message, String severity) {
        String prompt = "Explique cette alerte :\n"
                + "Type : " + alertType + "\n"
                + "Message : " + message + "\n"
                + "Sévérité : " + severity + "\n\n"
                + "Explique pourquoi elle a été déclenchée, les risques et les précautions.";
        return aiAgent.askAgent(new Prompt(prompt));
    }

    // Rapport
    public String generateReport(ReportRequest request) {
        String context = (request.getLocationId() != null)
                ? builderService.buildContext(request.getCityName(),request.getLocationId())
                : builderService.buildGlobalContext();

        String instruction = switch (request.getReportType() != null ? request.getReportType() : "DAILY") {
            case "WATER"  -> "Génère un rapport détaillé sur les ressources hydriques.";
            case "AIR"    -> "Génère un rapport sur la qualité de l'air.";
            case "WEEKLY" -> "Génère un rapport hebdomadaire complet.";
            default       -> "Génère un rapport environnemental journalier.";
        };

        String prompt = "Contexte EcoWatch :\n" + context + "\n\n" + instruction
                + "\nStructure : Résumé, Météo, Air, Eau, Alertes, Recommandations.";
        return aiAgent.askAgent(new Prompt(prompt));
    }

    // Comparaison des villes
    public String compareCity(Long id1, Long id2) {
        String ctx1 = builderService.buildContext( null,id1);
        String ctx2 = builderService.buildContext(null,id2);
        String prompt = "Compare ces deux villes :\n\nVILLE 1 :\n" + ctx1
                + "\n\nVILLE 2 :\n" + ctx2
                + "\n\nCompare météo, air, alertes. Recommande laquelle est la plus favorable.";
        return aiAgent.askAgent(new Prompt(prompt));
    }
}
