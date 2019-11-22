package no.nav.registre.syntrest.kubernetes;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Profile("ApplicationManagerTest")
@Configuration
public class ApplicationManagerTestConfig {
    @Bean
    RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
    @Bean
    KubernetesController kubernetesController() {
        return Mockito.mock(KubernetesController.class);
    }
    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(4);
    }
}
