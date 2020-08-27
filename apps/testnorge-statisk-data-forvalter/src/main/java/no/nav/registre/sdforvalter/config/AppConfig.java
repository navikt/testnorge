package no.nav.registre.sdforvalter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import no.nav.registere.testnorge.core.ApplicationCoreConfig;
import no.nav.registre.testnorge.database.config.FlywayConfiguration;
import no.nav.registre.testnorge.database.config.VaultHikariConfiguration;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "no.nav.registre.sdforvalter.database.repository")
@Import(value = {
        ApplicationCoreConfig.class,
        VaultHikariConfiguration.class,
        FlywayConfiguration.class
})
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
