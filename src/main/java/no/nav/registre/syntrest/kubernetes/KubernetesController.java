package no.nav.registre.syntrest.kubernetes;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.models.V1DeleteOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class KubernetesController {

    private final String GROUP = "nais.io";
    private final String VERSION = "v1alpha1";
    private final String NAMESPACE = "q2";
    private final String PLURAL = "applications";

    private final String manifestPath;
    private final RestTemplate restTemplate;
    private final UriTemplate isAliveUri;
    private final CustomObjectsApi api;

    private String isAliveUrl;
    private String dockerImagePath;
    private int maxRetries;
    private int retryDelay;

    public KubernetesController(RestTemplate restTemplate, ApiClient apiClient,
                                @Value("${isAlive}") String isAliveUrl,
                                @Value("${docker-image-path}") String dockerImagePath,
                                @Value("${max-alive-retries}") int maxRetries,
                                @Value("${alive-retry-delay}") int retryDelay) {

        this.restTemplate = restTemplate;
        this.manifestPath = "/nais/{appName}.yaml";
        this.isAliveUri = new UriTemplate(isAliveUrl);
        this.dockerImagePath = dockerImagePath;
        this.isAliveUrl = isAliveUrl;
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;

        this.api = new CustomObjectsApi();
        api.setApiClient(apiClient);
    }

    public void deployImage(String appName) throws ApiException, InterruptedException {
        Map<String, Object> manifestFile = prepareYaml(appName);

        if (!existsOnCluster(appName)) {
            api.createNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, manifestFile, null);
            log.info("Application \'{}\' created!", appName);
            waitForDeployment(appName);
        } else if (!isAlive(appName)) {
            waitForDeployment(appName);
        }
    }

    // TODO: REFACTOR
    public void takedownImage(String appName) throws ApiException, JsonSyntaxException {

        if (existsOnCluster(appName)) {
            V1DeleteOptions deleteOptions = new V1DeleteOptions();

            try {
                api.deleteNamespacedCustomObject(GROUP, VERSION, NAMESPACE, PLURAL, appName, deleteOptions,
                        null, null, null);
                log.info("Successfully deleted application \'{}\'", appName);

            } catch (JsonSyntaxException e) { // TODO: When does this happen?

                if (e.getCause() instanceof IllegalStateException) {
                    IllegalStateException ise = (IllegalStateException) e.getCause();
                    if (!Objects.isNull(ise.getMessage()) && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
                        log.info("Successfully deleted application \'{}\'", appName);
                    } else { throw e; }
                } else { throw e; }

            }

        } else {
            log.info("No application named \'{}\' found. Unable to delete.", appName);
            throw new IllegalArgumentException("No application named \'" + appName + "\' found. Unable to delete.");
        }
    }

    public boolean isAlive(String appName) {
        String response = "404";
        try {
            response = restTemplate.getForObject(isAliveUri.expand(appName), String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ignored) {}
        return "1".equals(response);
    }


    /////////// PRIVATE ///////////
    private boolean existsOnCluster(String appName) throws ApiException {
        List<String> applications = listApplicationsOnCluster();
        return applications.contains(appName);
    }

    // TODO: REFACTOR?
    private List<String> listApplicationsOnCluster() throws ApiException{

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
        log.info("Checking \'{}\'s deployment status...", appName);
        int numRetries = 0;

        while (!isAlive(appName)) {
            if (numRetries < maxRetries) {
                TimeUnit.SECONDS.sleep(retryDelay);
                log.info("Waiting for \'{}\' to deploy...", appName);
            } else {
                log.error("Application \'{}\' failed to deploy. Terminating...", appName);
                takedownImage(appName);
                return;
            }
            numRetries++;
        } // wend
    }

    // TODO: REFACTOR?
    private Map<String, Object> prepareYaml(String appName) {
        Yaml yaml = new Yaml();
        Map<String, Object> manifestFile = yaml.load(
                getClass().getResourceAsStream(manifestPath.replace("{appName}", appName)));

        /*Map<String, Object> spec = (Map) manifestFile.get("spec");
        String imageBase = spec.get("image").toString();
        String latestImage = imageBase.replace("latest", getLatestImageVersion(appName));
        spec.put("image", latestImage);*/

        return manifestFile;
    }

    // TODO: REFACTOR?
    private String getLatestImageVersion(String appName) {
        String query = String.format(dockerImagePath, appName);
        Map<String, Object> repositoryMap = (Map) restTemplate.getForObject(query, Object.class);
        List<String> tags = (List) repositoryMap.get("tags");
        return tags.get(tags.size() -1);
    }
}
