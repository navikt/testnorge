package no.nav.registre.syntrest.kubernetes;

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
    RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    @Value("${isAlive}")
    private String isAliveUrl;
    @Value("${docker-image-path}")
    private String dockerImagePath;
    @Value("${max-alive-retries}")
    private int maxRetries;
    @Value("${alive-retry-delay}")
    private int retryDelay;
    @Value("${github_username}")
    private String github_username;
    @Value("${github_password}")
    private String github_password;
    @Value("${proxy-url}")
    private String proxyUrl;
    @Value("${proxy-port}")
    private int proxyPort;

    @Bean
    KubernetesController kubernetesController() {
        var naisYaml = new NaisYaml(dockerImagePath);
        var githubConsumer = new GitHubConsumer(github_username, github_password, proxyUrl, proxyUrl, proxyPort, WebClient.builder());
        return new KubernetesController(customObjectsApi(), naisYaml, githubConsumer, isAliveUrl, dockerImagePath, maxRetries, retryDelay);
    }
}
