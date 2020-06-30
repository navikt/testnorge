package no.nav.registre.populasjoner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import no.nav.common.utils.Credentials;

@Configuration
@Profile("local")
public class LocalAppConfig {

    @Value("${SRV_USER}")
    private String user;

    @Value("${SRV_PASS}")
    private String pass;

    @Bean
    public Credentials serviceUserCredentials() {
        return new Credentials(user, pass);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
