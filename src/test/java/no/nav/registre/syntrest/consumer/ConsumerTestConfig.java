package no.nav.registre.syntrest.consumer;

import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Profile("ConsumerTest")
@Configuration
@PropertySource(value = "classpath:application-test.properties")
public class ConsumerTestConfig {

    @Bean
    public ApplicationManager applicationManager() {
        return Mockito.mock(ApplicationManager.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }

}
