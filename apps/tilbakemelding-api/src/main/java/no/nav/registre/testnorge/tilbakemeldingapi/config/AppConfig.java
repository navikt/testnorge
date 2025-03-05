package no.nav.registre.testnorge.tilbakemeldingapi.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Import(ApplicationCoreConfig.class)
public class AppConfig {

    @Bean
    public SlackConsumer slackConsumer(
            WebClient webClient,
            @Value("${consumers.slack.token}") String token,
            @Value("${consumers.slack.baseUrl}") String baseUrl,
            @Value("${http.proxy:#{null}}") String proxyHost
    ) {
        return new SlackConsumer(webClient, token, baseUrl, proxyHost);
    }

}
