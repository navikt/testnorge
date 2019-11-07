package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.CustomObjectsApi;
import org.mockito.Mockito;
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
}
