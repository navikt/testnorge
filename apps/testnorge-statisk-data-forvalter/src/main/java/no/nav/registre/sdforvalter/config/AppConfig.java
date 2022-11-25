package no.nav.registre.sdforvalter.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "no.nav.registre.sdforvalter.database.repository")
@Import(value = {
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
