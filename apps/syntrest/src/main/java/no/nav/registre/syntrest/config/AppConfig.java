package no.nav.registre.syntrest.config;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.kubernetes.KubernetesController;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@Slf4j
public class AppConfig {

    private static final int TIMEOUT = 240_000;
    private final int EXECUTOR_POOL_SIZE = 4;
    @Value("${kube-config-path}")
    private String kubeConfigPath;
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
                                .setConnectTimeout(TIMEOUT)
                                .setSocketTimeout(TIMEOUT)
                                .setConnectionRequestTimeout(TIMEOUT)
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
    @DependsOn("apiClient")
    CustomObjectsApi customObjectsApi() {
        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(apiClient());
        return api;
    }

    @Bean
    RestTemplateBuilder restTemplateBuilder() { return new RestTemplateBuilder(); }

    @Bean
    @DependsOn({"restTemplateBuilder", "customObjectsApi"})
    public KubernetesController kubernetesController() {
        return new KubernetesController(restTemplateBuilder(), customObjectsApi(), github_username, github_password,
                proxyUrl, proxyPort, isAliveUrl, dockerImagePath, maxRetries, retryDelay);
    }


    @Bean
    @DependsOn({"kubernetesController", "scheduledExecutorService"})
    public ApplicationManager applicationManager() {
        return new ApplicationManager(kubernetesController(), scheduledExecutorService());
    }


    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer aaregConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-aareg");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer bisysConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-arena-bisys");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer instConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-inst");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer medlConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-medl");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer meldekortConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-arena-meldekort");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer navConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-nav");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer poppConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-popp");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer samConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-sam");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer inntektConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-inntekt");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer tpConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-tp");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer tpsConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-tps");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer frikortConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-frikort");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer aMeldingConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-amelding");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer aMeldingStartConsumer() {
        return new SyntConsumer(applicationManager(), "synthdata-amelding");
    }
}
