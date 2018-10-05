package no.nav.registre.orkestratoren;

import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.provider.rs.InternalController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({InternalController.class, JobController.class, TpsfConsumer.class})
public class AppConfig {

    @Value("${spring.credentials.username}")
    private String username;
    @Value("${spring.credentials.password}")
    private String password;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username,password));
        return restTemplate;
    }
}
