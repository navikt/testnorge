package no.nav.registre.testnorge.personexportapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletcore.config.ApplicationProperties;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class AppConfig {
    @Bean
    public SlackConsumer slackConsumer(
            @Value("${consumers.slack.token}") String token,
            @Value("${consumers.slack.baseUrl}") String baseUrl,
            @Value("${http.proxy:#{null}}") String proxyHost,
            ApplicationProperties properties
    ) {
        return new SlackConsumer(token, baseUrl, proxyHost, properties.getName());
    }
}
