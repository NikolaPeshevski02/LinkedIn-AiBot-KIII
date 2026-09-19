package mk.ukim.finki.aibotbackend.bot.extraction;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mk.ukim.finki.aibotbackend.bot.browser.PageSnapshot;
import mk.ukim.finki.aibotbackend.bot.llm.BotDecision;
import mk.ukim.finki.aibotbackend.bot.llm.LlmClient;
import mk.ukim.finki.aibotbackend.model.dto.CreateExtractedPostDto;
import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with an extractor for your assigned social network.
 */
@Component
public class LinkedInContentExtractor implements ContentExtractor {
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public LinkedInContentExtractor(LlmClient llmClient, ObjectMapper objectMapper) {
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<CreateExtractedPostDto> extract(PageSnapshot snapshot) {

        String systemPrompt = """
                You are a LinkedIn content extraction AI.

                Your task is to extract LinkedIn posts from the provided page.

                Return ONLY valid JSON.

                The JSON must be an array of objects matching this structure:

                [
                  {
                    "externalId": "unique post id if available",
                    "authorHandle": "author name or username",
                    "content": "text content of the post",
                    "sourceUrl": "URL of the post",
                    "postedAt": null,
                    "macedonianConfidence": null,
                    "mediaItems": [
                      { 
                        "type": "IMAGE",
                        "sourceUrl": "media url",
                        "storagePath": null
                      }
                    ]
                  }
                ]

                Rules:
                - Extract only actual LinkedIn posts.
                - Ignore navigation, buttons, advertisements, and recommendations.
                - Do not calculate macedonianConfidence. Always return null.
                - Media type can only be IMAGE or VIDEO.
                - If there is no media, return an empty array.
                - Never invent URLs. If a media URL is unavailable, omit the media item.
                - Unknown fields should be null.
                """;


        String userPrompt = """
                Page URL:
                %s

                Page title:
                %s

                DOM content:
                %s
                """.formatted(
                snapshot.url(),
                snapshot.title(),
                snapshot.domContent()
        );


        String response = llmClient.complete(
                systemPrompt,
                userPrompt
        );

        try {
            return objectMapper.readValue(
                    response,
                    new TypeReference<List<CreateExtractedPostDto>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to extract LinkedIn posts",
                    e
            );
        }
}
}
