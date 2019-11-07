package no.nav.registre.syntrest.consumer;

import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Profile("ConsumerTest")
@Configuration
public class ConsumerTestConfig {

    @Bean
    public ApplicationManager applicationManager() {
        return Mockito.mock(ApplicationManager.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    public SyntConsumerManager syntConsumerManager(ApplicationManager manager, RestTemplate template) {
        return new SyntConsumerManager(manager, template);
    }
}
