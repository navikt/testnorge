package no.nav.registre.syntrest.config;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.CustomObjectsApi;
import no.nav.registre.syntrest.consumer.GitHubConsumer;
import no.nav.registre.syntrest.utils.NaisYaml;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.MalformedURLException;

@Profile("kubernetesTest")
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

}
