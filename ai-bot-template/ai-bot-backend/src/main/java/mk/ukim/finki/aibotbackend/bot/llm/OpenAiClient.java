package mk.ukim.finki.aibotbackend.bot.llm;

    import java.util.List;

    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import mk.ukim.finki.aibotbackend.bot.browser.PageSnapshot;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Component;
    import org.springframework.web.client.RestClient;
    import com.fasterxml.jackson.databind.ObjectMapper;


/**
     * Placeholder so the application boots before the assignment is implemented.
     * TODO(student): Replace this bean with an implementation backed by a real LLM.
     */
    @Component
    public class OpenAiClient implements LlmClient {
        private final RestClient restClient;
        private final String apiKey;
        private final String baseUrl;
        private final ObjectMapper objectMapper;

    public OpenAiClient(RestClient restClient, @Value ("${llm.api-key}") String apiKey, @Value ("${llm.base-url}") String baseUrl, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
    }


    @Override
        public String complete(String systemPrompt, String userPrompt) {
        System.out.println("========== LLM CONFIG ==========");
        System.out.println("BASE URL: " + baseUrl);
        System.out.println("KEY LENGTH: " + apiKey.length());
        System.out.println("KEY PREFIX: " + apiKey.substring(0, Math.min(8, apiKey.length())));
        System.out.println("=================================");
        OpenAiRequest request = new OpenAiRequest(
                "gemma4:31b",
                List.of(
                        new Message("system",systemPrompt),
                        new Message("user",userPrompt)
                )
        );
        OpenAiResponse response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(request)
                .retrieve()
                .body(OpenAiResponse.class);

        System.out.println("========== OLLAMA RESPONSE ==========");
        System.out.println(response);
        System.out.println("=====================================");

        if(response == null) throw new RuntimeException("response is null");
        return response.choices().getFirst().message().content();
        };
    /**
     * The core of the agentic loop: given the current page, the goal for the
     * current extraction target and the actions performed so far, decide what
     * the bot should do next.
     *
     * <p>Implementations typically serialise the snapshot and history into a
     * prompt, ask the model for a structured (e.g. JSON) response, and parse
     * it into a {@link BotDecision}.</p>
     */
        @Override
        public BotDecision decideNextAction(PageSnapshot snapshot, String goal, List<BotAction> history) {
            String systemPrompt = """
You are an autonomous browser automation agent.

Your task is to decide the next browser action needed to accomplish the user's goal.

Allowed action types:
- NAVIGATE
- CLICK
- TYPE
- SCROLL
- WAIT
- EXTRACT
- LOGIN
- FINISH

Return ONLY valid JSON.
Do not wrap the JSON in markdown code fences.
Do not include any explanation before or after the JSON.
The JSON must exactly match this schema:

{
  "action": {
    "type": "CLICK",
    "target": "button.login",
    "value": null,
    "reasoning": "Click the login button."
  },
  "goalReached": false,
  "rationale": "The user is not logged in yet."
}

Rules:
- Use only one of the allowed action types.
- target is null if the action does not need one.
- value is only used for TYPE.
- reasoning explains why that specific action was chosen.
- rationale explains the overall decision.
- If the goal has been achieved, set goalReached=true and action.type=FINISH.
- Output ONLY JSON and nothing else.

""";

            String userPrompt = """
Goal:
%s

Current URL:
%s

Page title:
%s

DOM:
%s

Previous actions:
%s

ScreenshotBase64:
%s
""".formatted(
                    goal,
                    snapshot.url(),
                    snapshot.title(),
                    snapshot.domContent(),
                    history,
                    snapshot.screenshotBase64()
            );

            String json = complete(systemPrompt, userPrompt);
            //Fo
            json = json
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            System.out.println("========== LLM JSON ==========");
            System.out.println("[" + json + "]");
            System.out.println("Length: " + json.length());
            System.out.println("==============================");

            ObjectMapper mapper = new ObjectMapper();

            try {
                return mapper.readValue(json, BotDecision.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse BotDecision", e);
            }

    }
    }
