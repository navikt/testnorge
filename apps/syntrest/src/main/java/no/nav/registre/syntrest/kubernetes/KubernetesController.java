package no.nav.registre.syntrest.kubernetes;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.models.V1DeleteOptions;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.GitHubConsumer;
import no.nav.registre.syntrest.utils.NaisYaml;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;
import reactor.netty.http.client.HttpClient;

import java.net.ProxySelector;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

@Slf4j
public class KubernetesController {

    private static final int TIMEOUT = 240_000;  // in milliseconds
    private final String GROUP = "nais.io";
    private final String VERSION = "v1alpha1";
    private final String NAMESPACE = "q2";
    private final String PLURAL = "applications";

    private final String isAliveUrl;
    private final CustomObjectsApi api;
    private final int maxRetries;
    private final int retryDelay;

    private final NaisYaml naisYaml;
    private final GitHubConsumer gitHubConsumer;

    private final WebClient webClient;


    public KubernetesController(RestTemplateBuilder restTemplateBuilder,
                                CustomObjectsApi customObjectsApi,
                                NaisYaml naisYaml,
                                GitHubConsumer gitHubConsumer,
                                @Value("${isAlive}") String isAliveUrl,
                                @Value("${docker-image-path}") String dockerImagePath,
                                @Value("${max-alive-retries}") int maxRetries,
                                @Value("${alive-retry-delay}") int retryDelay
            ) {

        this.naisYaml = naisYaml;
        this.gitHubConsumer = gitHubConsumer;
        this.isAliveUrl = isAliveUrl;
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
        this.api = customObjectsApi;

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(TIMEOUT));
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        this.webClient = WebClient.builder().clientConnector(connector).build();
    }

    public void deployImage(String appName) throws ApiException, InterruptedException {
        String imageTag = gitHubConsumer.getApplicationTag(appName);
        Map<String, Object> manifestFile = naisYaml.provideYaml(appName, imageTag);

        if (!existsOnCluster(appName)) {
            api.createNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, manifestFile, null);
            log.info("Application '{}' created!", appName);
            waitForDeployment(appName);
        } else if (!isAlive(appName)) {
            waitForDeployment(appName);
        }
    }

    public void takedownImage(String appName) throws ApiException, JsonSyntaxException {

        if (existsOnCluster(appName)) {
            V1DeleteOptions deleteOptions = new V1DeleteOptions();
            try {
                api.deleteNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, appName, deleteOptions,
                        null, null, null);
                log.info("Successfully deleted application \'{}\'", appName);

            } catch (JsonSyntaxException e) {
                if (e.getCause() instanceof IllegalStateException) {
                    IllegalStateException ise = (IllegalStateException) e.getCause();
                    if (!isNull(ise.getMessage()) && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
                        log.info("Successfully deleted application '{}'", appName);
                    } else {
                        throw e;
                    }
                } else {
                    throw e;
                }
            }

        } else {
            log.info("No application named '{}' found. Unable to delete.", appName);
            throw new IllegalArgumentException("No application named '" + appName + "' found. Unable to delete.");
        }
    }

    public boolean isAlive(String appName) {
        String response = webClient.get()
                .uri(isAliveUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return "1".equals(response);
    }

    public boolean existsOnCluster(String appName) throws ApiException {
        List<String> applications = listApplicationsOnCluster();
        return applications.contains(appName);
    }


    private List<String> listApplicationsOnCluster() throws ApiException {

        List<String> applications = new ArrayList<>();

        LinkedTreeMap result = (LinkedTreeMap) api.listNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL,
                null, null, null, null);
        ArrayList items = (ArrayList) result.get("items");

        for (Object item : items) {
            LinkedTreeMap app = (LinkedTreeMap) item;
            LinkedTreeMap metadata = (LinkedTreeMap) app.get("metadata");
            String name = (String) metadata.get("name");

            applications.add(name);
        }

        return applications;
    }

    private void waitForDeployment(String appName) throws InterruptedException, ApiException {
        log.info("Checking '{}'s deployment status...", appName);
        int numRetries = 0;

        log.info("Waiting for '{}' to deploy... (max {} seconds)", appName, (maxRetries * retryDelay));
        while (!isAlive(appName)) {
            if (numRetries < maxRetries) {
                TimeUnit.SECONDS.sleep(retryDelay);
            } else {
                log.error("Application '{}' failed to deploy. Terminating...", appName);
                takedownImage(appName);
                return;
            }
            numRetries++;
        }
        log.info("Application '{}' deployed successfully!", appName);
    }
}
