package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.CustomObjectsApi;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Profile("KubernetesTest")
@Configuration
public class KubernetesTestConfig {
    @Bean
    ApiClient apiClient() {
        return Mockito.mock(ApiClient.class);
    }
    @Bean
    CustomObjectsApi customObjectsApi() {
        return Mockito.mock(CustomObjectsApi.class);
    }
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value("${isAlive}")
    private String isAliveUrl;
    @Value("${docker-image-path}")
    private String dockerImagePath;
    @Value("${max-alive-retries}")
    private int maxRetries;
    @Value("${alive-retry-delay}")
    private int retryDelay;
    @Bean
    KubernetesController kubernetesController() {
        return new KubernetesController(customObjectsApi(), isAliveUrl, dockerImagePath, maxRetries, retryDelay);
    }
}
