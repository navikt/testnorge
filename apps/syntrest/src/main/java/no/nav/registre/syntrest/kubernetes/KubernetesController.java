package no.nav.registre.syntrest.kubernetes;

import com.google.gson.JsonSyntaxException;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.models.V1DeleteOptions;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.GitHubConsumer;
import no.nav.registre.syntrest.kubernetes.command.GetIsAliveCommand;
import no.nav.registre.syntrest.kubernetes.command.GetIsAliveWithRetryCommand;
import no.nav.registre.syntrest.kubernetes.exception.KubernetesException;
import no.nav.registre.syntrest.utils.NaisYaml;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class KubernetesController {

    private static final int TIMEOUT_IN_MILLISECONSDS = 240_000;
    private static final String GROUP = "nais.io";
    private static final String VERSION = "v1alpha1";
    private static final String NAMESPACE = "dolly";
    private static final String PLURAL = "applications";

    private final String isAliveUrl;
    private final CustomObjectsApi api;
    private final int maxRetries;
    private final int retryDelay;

    private final NaisYaml naisYaml;
    private final GitHubConsumer gitHubConsumer;

    private final WebClient webClient;

    public KubernetesController(
            CustomObjectsApi customObjectsApi,
            NaisYaml naisYaml,
            GitHubConsumer gitHubConsumer,
            @Value("${isAlive}") String isAliveUrl,
            @Value("${max-alive-retries}") int maxRetries,
            @Value("${alive-retry-delay}") int retryDelay
    ) {
        this.naisYaml = naisYaml;
        this.gitHubConsumer = gitHubConsumer;
        this.isAliveUrl = isAliveUrl;
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
        this.api = customObjectsApi;

        var httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(TIMEOUT_IN_MILLISECONSDS));
        var connector = new ReactorClientHttpConnector(httpClient);
        this.webClient = WebClient.builder().clientConnector(connector).build();
    }

    public void deployImage(String appName) throws ApiException {
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

    public void takedownImage(String appName) throws ApiException {

        if (existsOnCluster(appName)) {
            var deleteOptions = new V1DeleteOptions();
            try {
                api.deleteNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, appName, deleteOptions,
                        null, null, null);
                log.info("Successfully deleted application '{}'", appName);

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
        return new GetIsAliveCommand(webClient, isAliveUrl, appName).call();
    }

    private void pollApplication(String appName) {
        String response = new GetIsAliveWithRetryCommand(webClient, isAliveUrl, appName, maxRetries, retryDelay).call();

        if (!"1".equals(response)) {
            throw new KubernetesException("Application failed to deploy.");
        }
    }

    public boolean existsOnCluster(String appName) throws ApiException {
        List<String> applications = listApplicationsOnCluster();
        return applications.contains(appName);
    }

    private List<String> listApplicationsOnCluster() throws ApiException {

        List<String> applications = new ArrayList<>();

        Map result = (Map) api.listNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL,
                null, null, null, null);
        List items = (List) result.get("items");

        for (Object item : items) {
            Map app = (Map) item;
            Map metadata = (Map) app.get("metadata");
            String name = (String) metadata.get("name");

            applications.add(name);
        }

        return applications;
    }

    private void waitForDeployment(String appName) throws ApiException {
        log.info("Waiting for '{}' to deploy... (max {} seconds)", appName, (maxRetries * retryDelay));
        try {
            pollApplication(appName);
        } catch (RuntimeException e) {
            log.error("Application '{}' failed to deploy. Terminating...", appName);
            takedownImage(appName);
            throw e;
        }
        log.info("Application '{}' deployed successfully!", appName);
    }
}
