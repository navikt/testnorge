package no.nav.registre.orkestratoren;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.provider.rs.InternalController;

@Configuration
@Import({ InternalController.class, JobController.class, TpsfConsumer.class })
public class AppConfig {

    @Value("${orkestratoren.credentials.username}")
    private String username;

    @Value("${orkestratoren.credentials.password}")
    private String password;

    @Value("${tpsf.credentials.username}")
    private String tpsfUsername;

    @Value("${tpsf.credentials.password}")
    private String tpsfPassword;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        return restTemplate;
    }

    @Bean
    public RestTemplate restTemplateTpsf() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(tpsfUsername, tpsfPassword));
        return restTemplate;
    }
}
