package ma.enset.aiagentservice.agent;

import ma.enset.aiagentservice.tools.EcowatchTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class AiAgent {
    private final ChatClient  chatClient;
    
    public AiAgent(ChatClient.Builder builder, ChatMemory memory,EcowatchTools ecowatchTools) {

        ToolCallbackProvider provider= MethodToolCallbackProvider.builder()
                .toolObjects(ecowatchTools).build();
        this.chatClient = builder
                .defaultSystem("""
                         Tu es EcoWatch AI, un assistant environnemental expert pour le Maroc.
                         Ton rôle est d'analyser les données en temps réel du système EcoWatch :
                           - Météo actuelle et prévisions 5 jours
                           - Qualité de l'air (IQA), poussières PM2.5, CO2, température et humidité du sol
                           - État des barrages, lacs, rivières et nappes phréatiques
                           - Alertes environnementales actives
                         Tes règles :
                          1. Réponds dois répondre selon le langue de l'utilisateur,si l'utilisateur pose une question en francais, répond lui en francais, 
                          si l'utilisateur pose une question en arabe , répond lui en arabe, si l'utilisateur pose une question en anglais, tu dois répondre en anglais de façon claire et structurée.
                          si l'utilisateur pose une question avec d'autre langue , tu le répond par défaut en francais
                          2. Base-toi UNIQUEMENT sur les données fournies dans le contexte — ne devine pas.
                          3. Si une donnée est manquante, dis-le honnêtement.
                          4. Donne des recommandations pratiques quand c'est pertinent.
                          5. Sois concis mais complet. Utilise des listes quand c'est plus lisible.
                          6. Pour les alertes, explique pourquoi elles ont été déclenchées et leurs conséquences.
                          8. Si l'utilisateur demande des informations (météo, qualité de l'air, alertes) pour PLUSIEURS villes
                             dans la même question, appelle l'outil approprié une fois PAR ville mentionnée, séparément ,
                             ne te contente jamais du résumé global si des villes précises sont nommées.
                         Tu peux aider avec :
                            - Analyse météo et conseils du jour
                            - Explication des alertes environnementales
                            - Comparaison entre villes
                            - État des ressources en eau
                            - Conseils personnalisés (sport, sortie, santé)
                            - Prédictions et tendances basées sur les prévisions
                            - Génération de rapports environnementaux
                         Réponds toujours en français, de façon concise et structurée.
                         Si une donnée manque, dis-le clairement sans inventer.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build()
                )
                .defaultToolCallbacks(provider)
                .build();
    }
    public String askAgent(Prompt prompt){
        String raw = chatClient.prompt(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,"ecowatch-default"))
                .call().content();
        return cleanMarkdown(raw);
    }
    // ******
    private static String cleanMarkdown(String text) {
        if (text == null || text.isBlank()) return text;
        String cleaned = text;

        // Titres markdown (#, ##, ###...)
        cleaned = cleaned.replaceAll("(?m)^\\s{0,3}#{1,6}\\s*", "");

        // Gras / italique (**texte**, __texte__, *texte*)
        cleaned = cleaned.replaceAll("\\*\\*(.+?)\\*\\*", "$1");
        cleaned = cleaned.replaceAll("__(.+?)__", "$1");
        cleaned = cleaned.replaceAll("(?<!\\*)\\*([^*\\n]+)\\*(?!\\*)", "$1");

        // Lignes de séparation de tableau markdown ( |---|---| )
        cleaned = cleaned.replaceAll("(?m)^[ \\t]*\\|?[ \\t:\\-|]+\\|[ \\t:\\-|]*[ \\t]*\\r?\\n?", "");

        // Lignes de tableau restantes "| a | b | c |" -> "- a — b — c"
        Pattern tableRow = Pattern.compile("(?m)^[ \\t]*\\|(.+)\\|[ \\t]*$");
        Matcher m = tableRow.matcher(cleaned);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String[] cells = m.group(1).split("\\|");
            StringBuilder row = new StringBuilder("- ");
            boolean first = true;
            for (String cell : cells) {
                String value = cell.trim();
                if (value.isEmpty()) continue;
                if (!first) row.append(" — ");
                row.append(value);
                first = false;
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(row.toString()));
        }
        m.appendTail(sb);
        cleaned = sb.toString();

        // Lignes de tirets utilisées comme simples séparateurs (----)
        cleaned = cleaned.replaceAll("(?m)^[ \\t]*-{3,}[ \\t]*$\\r?\\n?", "");

        // Nettoyage des sauts de ligne en trop laissés par les suppressions
        cleaned = cleaned.replaceAll("\\n{3,}", "\n\n").trim();

        return cleaned;
    }
}
