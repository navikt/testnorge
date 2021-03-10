package no.nav.registre.testnorge.rapportering.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.core.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.database.config.FlywayConfiguration;
import no.nav.registre.testnorge.libs.database.config.VaultHikariConfiguration;
import no.nav.registre.testnorge.libs.slack.consumer.SlackConsumer;

@EnableJpaAuditing
@Configuration
@EnableScheduling
@Import(value = {
        ApplicationCoreConfig.class,
        VaultHikariConfiguration.class,
        FlywayConfiguration.class
})
public class AppConfig {

    @Bean
    public SlackConsumer slackConsumer(
            @Value("${consumer.slack.token}") String token,
            @Value("${consumer.slack.baseUrl}") String baseUrl,
            @Value("${http.proxy:#{null}}") String proxyHost,
            ApplicationProperties properties
    ) {
        return new SlackConsumer(token, baseUrl, proxyHost, properties.getName());
    }
}
