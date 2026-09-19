package mk.ukim.finki.aibotbackend.bot.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "login")
public record LoginProperties(
        String username,
        String password
) {
}
