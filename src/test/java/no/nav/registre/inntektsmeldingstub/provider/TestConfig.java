package no.nav.registre.inntektsmeldingstub.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("controllerTest")
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "no.nav.registre.inntektsmeldingstub.database.repository")
public class TestConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
