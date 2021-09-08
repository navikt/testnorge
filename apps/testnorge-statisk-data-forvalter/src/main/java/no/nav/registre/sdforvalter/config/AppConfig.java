package no.nav.registre.sdforvalter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.database.config.FlywayConfiguration;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;
import no.nav.testnav.libs.servletsecurity.config.InsecureOAuth2ServerToServerConfiguration;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "no.nav.registre.sdforvalter.database.repository")
@Import(value = {
        ApplicationCoreConfig.class,
        InsecureOAuth2ServerToServerConfiguration.class
})
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
