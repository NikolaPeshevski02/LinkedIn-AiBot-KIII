package mk.ukim.finki.aibotbackend.bot.extraction;

import mk.ukim.finki.aibotbackend.bot.llm.LlmClient;
import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with a real language detector.
 */
@Component
public class StubLanguageDetector implements LanguageDetector {
    private final LlmClient llmClient;

    public StubLanguageDetector(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public double macedonianConfidence(String text) {

        String systemPrompt = """
                You are an expert language detection system.
                
                Determine the probability that the provided text is written in Macedonian.
                
                Return ONLY a decimal number between 0.0 and 1.0.
                
                Interpretation:
                - 1.0 = definitely Macedonian
                - 0.0 = definitely not Macedonian
                
                Consider:
                - vocabulary
                - spelling
                - grammar
                - alphabet
                - common Macedonian expressions
                
                Distinguish Macedonian from closely related languages such as Serbian, Bulgarian, Croatian and Bosnian.
                
                Give extra weight to letters that are characteristic of the Macedonian alphabet, especially:
                - ѓ
                - ќ
                - ѕ
                
                The presence of these letters is strong evidence for Macedonian, but their absence does not necessarily mean the text is not Macedonian.
                
                If the text is too short or ambiguous, return an intermediate confidence.
                
                Output ONLY the number and nothing else.
                """;

        String userPrompt = """
                Text:
                
                %s
                """.formatted(text);

        String response = llmClient.complete(systemPrompt, userPrompt);

        try {
            return Double.parseDouble(response.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("LLM returned an invalid confidence: " + response, e);
        }
    }
}
