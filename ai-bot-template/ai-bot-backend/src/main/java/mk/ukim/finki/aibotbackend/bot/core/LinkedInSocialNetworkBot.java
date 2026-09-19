package mk.ukim.finki.aibotbackend.bot.core;

import mk.ukim.finki.aibotbackend.bot.browser.BrowserAgent;
import mk.ukim.finki.aibotbackend.bot.extraction.ContentExtractor;
import mk.ukim.finki.aibotbackend.bot.extraction.LanguageDetector;
import mk.ukim.finki.aibotbackend.bot.llm.LlmClient;
import mk.ukim.finki.aibotbackend.config.BotProperties;
import mk.ukim.finki.aibotbackend.model.domain.ExtractionTarget;
import mk.ukim.finki.aibotbackend.model.enums.SocialNetwork;
import org.springframework.stereotype.Component;

/**
 * Placeholder so the application boots before the assignment is implemented.
 *
 * <p>TODO(student): Replace this bean with a bot for YOUR assigned social
 * network, e.g. {@code InstagramBot extends AbstractSocialNetworkBot}, and
 * implement {@link #network()}, {@link #login()} and
 * {@link #buildGoal(ExtractionTarget)}. Do not override
 * {@code execute(...)} — the loop is shared.</p>
 */
@Component
public class LinkedInSocialNetworkBot extends AbstractSocialNetworkBot {
    private final LoginProperties loginProperties;

    public LinkedInSocialNetworkBot(
        BrowserAgent browserAgent,
        LlmClient llmClient,
        ContentExtractor contentExtractor,
        LanguageDetector languageDetector,
        BotProperties botProperties,

        LoginProperties loginProperties) {
        super(browserAgent, llmClient, contentExtractor, languageDetector, botProperties);
        this.loginProperties = loginProperties;
    }

    @Override
    public SocialNetwork network() {
        return SocialNetwork.LINKEDIN;
    }

    @Override
    public void login()  {

            browserAgent.navigateTo("https://www.linkedin.com/login/");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        browserAgent.type("input[type='email']", loginProperties.username());

        browserAgent.type("input[type='password']", loginProperties.password());

        browserAgent.click("xpath=//button[normalize-space(.)='Sign in']");

    }

    @Override
    protected String buildGoal(ExtractionTarget target) {

            return switch (target.getType()) {
                case PROFILE ->
                        "Navigate to the profile '" + target.getValue()
                                + "' and extract Macedonian-language content from its posts.";

                case HASHTAG ->
                        "Search for the hashtag '" + target.getValue()
                                + "' and extract Macedonian-language posts and media.";

                case KEYWORD ->
                        "Search for content containing the keyword '" + target.getValue()
                                + "' and extract relevant Macedonian-language posts and media.";

                case FEED_URL ->
                        "Open the feed URL '" + target.getValue()
                                + "' and extract relevant Macedonian-language content.";

        }    ;
    }
}
