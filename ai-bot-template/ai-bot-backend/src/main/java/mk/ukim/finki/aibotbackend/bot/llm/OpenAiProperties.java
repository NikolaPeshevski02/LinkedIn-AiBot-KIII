package mk.ukim.finki.aibotbackend.bot.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "llm")
public record OpenAiProperties(
        String baseUrl,
        String apiKey
) {
}
