package no.nav.registre.syntrest.config;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import java.io.FileReader;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Import(ApplicationCoreConfig.class)
public class AppConfig {

    private static final int TIMEOUT_IN_MILLIS = 240_000;
    private final int EXECUTOR_POOL_SIZE = 4;
    @Value("${kube-config-path}")
    private String kubeConfigPath;

    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
                        .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                        .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setConnectTimeout(TIMEOUT_IN_MILLIS)
                                .setSocketTimeout(TIMEOUT_IN_MILLIS)
                                .setConnectionRequestTimeout(TIMEOUT_IN_MILLIS)
                                .build())
                        .setMaxConnPerRoute(2000)
                        .setMaxConnTotal(5000)
                        .build()))
                .build();
    }

    @Bean
    ApiClient apiClient() {
        try {
            KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath));
            kc.setContext("dev-fss");
            return Config.fromConfig(kc);
        } catch (IOException e) {
            String errormsg = String.format("Could not apply configuration from %s", kubeConfigPath);
            log.error("Could not apply configuration from {}.", kubeConfigPath);
            throw new BeanCreationException(errormsg, e);
        }
    }

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean UriBuilderFactory uriFactory() {
        return new DefaultUriBuilderFactory();
    }

    @Bean
    @DependsOn("apiClient")
    CustomObjectsApi customObjectsApi() {
        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(apiClient());
        return api;
    }

    @Bean
    RestTemplateBuilder restTemplateBuilder() { return new RestTemplateBuilder(); }
}
