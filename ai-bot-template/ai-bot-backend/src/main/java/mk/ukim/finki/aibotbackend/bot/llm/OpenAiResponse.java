package mk.ukim.finki.aibotbackend.bot.llm;

import java.util.List;

public record OpenAiResponse(
        List<Choice> choices
) {

}
