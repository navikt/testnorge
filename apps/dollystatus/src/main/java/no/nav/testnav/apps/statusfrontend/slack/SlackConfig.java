package no.nav.testnav.apps.statusfrontend.slack;

import com.slack.api.Slack;
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
    Slack slack() {
        return Slack.getInstance();
    }

    @Bean
    SlackService slackService(Slack slack) {
        return new SlackService(slack, token, channel);
    }

}
