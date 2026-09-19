package mk.ukim.finki.aibotbackend.bot.llm;

import java.util.List;

public record OpenAiRequest(String model,
                            List<Message> messages) {
}
