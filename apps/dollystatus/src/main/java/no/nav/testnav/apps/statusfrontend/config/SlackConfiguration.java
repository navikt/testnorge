package no.nav.testnav.apps.statusfrontend.config;

import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(prefix = "slack", name = "enabled", havingValue = "true")
public class SlackConfiguration {

    @Bean
    SlackConsumer slackConsumer(
            WebClient webClient,
            SlackProperties properties
    ) {
        if (properties.getToken().isBlank() || properties.getChannel().isBlank()) {
            throw new IllegalStateException("Slack er aktivert uten token eller kanal.");
        }
        return new SlackConsumer(
                webClient,
                properties.getToken(),
                properties.getBaseUrl(),
                null);
    }
}
