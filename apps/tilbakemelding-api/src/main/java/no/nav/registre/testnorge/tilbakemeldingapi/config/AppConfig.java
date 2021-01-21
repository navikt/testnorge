package no.nav.registre.testnorge.tilbakemeldingapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.List;

import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.AnalysisAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.core.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.registre.testnorge.libs.slack.consumer.SlackConsumer;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
        AnalysisAutoConfiguration.class
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
