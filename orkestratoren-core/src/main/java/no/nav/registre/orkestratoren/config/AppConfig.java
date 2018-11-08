package no.nav.registre.orkestratoren.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.orkestratoren.JobController;
import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.provider.rs.InternalController;

@Configuration
@Import({ InternalController.class, JobController.class, TpsfConsumer.class })
public class AppConfig {

    @Value("${orkestratorens.ida.credential.username}")
    private String tpsfUsername;

    @Value("${orkestratorens.ida.credential.password}")
    private String tpsfPassword;

    @Bean
    public RestTemplate restTemplateTpsf() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(tpsfUsername, tpsfPassword));
        return restTemplate;
    }
}
