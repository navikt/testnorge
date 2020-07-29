package no.nav.registre.populasjoner.config;

import no.nav.common.utils.Credentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("local")
public class LocalAppConfig {

    @Value("${service.username}")
    private String user;

    @Value("${service.password}")
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
