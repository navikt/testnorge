package no.nav.testnav.apps.statusfrontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    @Value("${app.slack.bot-token}")
    private String token;

    @Value("${app.slack.channel-id}")
    private String channel;

    @Bean
    SlackService slackService() {
        return new SlackService(token, channel);
    }

}
