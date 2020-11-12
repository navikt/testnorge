package no.nav.registre.testnorge.tilbakemeldingapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

import no.nav.registre.testnorge.libs.autodependencyanalysis.config.AutoRegistrationDependencyAnalysisConfiguration;
import no.nav.registre.testnorge.libs.autodependencyanalysis.service.ApplicationRegistrationService;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.core.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.registre.testnorge.libs.slack.consumer.SlackConsumer;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@RequiredArgsConstructor
public class AppConfig {
    private final ApplicationRegistrationService applicationRegistrationService;

    @Bean
    public SlackConsumer slackConsumer(
            @Value("${consumer.slack.token}") String token,
            @Value("${consumer.slack.baseUrl}") String baseUrl,
            @Value("${http.proxy:#{null}}") String proxyHost,
            ApplicationProperties properties
    ) {
        return new SlackConsumer(token, baseUrl, proxyHost, properties.getName());
    }

    @PostConstruct
    public void init() {
        applicationRegistrationService.register();
    }
}
