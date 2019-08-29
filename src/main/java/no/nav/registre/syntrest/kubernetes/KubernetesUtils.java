package no.nav.registre.syntrest.kubernetes;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.services.IService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public class KubernetesUtils {

    public ApiClient createApiClient() throws IOException {
        KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader("/var/run/secrets/nais.io/vault/kubeconfig"));
        ApiClient client = Config.fromConfig(kc);
        return client;
    }

    @Value("${max-alive-retries}")
    private int maxRetries;

    @Value("${alive-retry-delay}")
    private int retryDelay;

    public void createApplication(ApiClient client, String manifestPath, IService serviceObject) throws InterruptedException, ApiException {
        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);
        Yaml yaml = new Yaml();
        Map<String, Object> manifestFile = yaml.load(getClass().getResourceAsStream(manifestPath));

        Map<String, String> metadata = (Map) manifestFile.get("metadata");
        String appName = metadata.get("name");

        Map<String, Object> spec = (Map) manifestFile.get("spec");
        String imageBase = spec.get("image").toString();
        String latestImage = imageBase.replace("latest", getLatestImageVersion(appName));
        spec.put("image", latestImage);

        if (!applicationExists(client, appName)) {
            try {
                api.createNamespacedCustomObject("nais.io", "v1alpha1", "q2", "applications", manifestFile, null);
                log.info("Application: " + appName + " created!");
                waitForIsAlive(client, appName, serviceObject);
            } catch (ApiException e) {
                log.info(e.getResponseBody());
            } catch (Exception e) {
                log.info(e.toString());
            }
        } else if (!applicationIsAlive(serviceObject)) {
            waitForIsAlive(client, appName, serviceObject);
        }
    }

    public String getLatestImageVersion(String appName) {
        String query = String.format("https://docker.adeo.no:5000/v2/registre/%s/tags/list", appName);
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> repositoryMap = (Map) restTemplate.getForObject(query, Object.class);
        List<String> tags = (List) repositoryMap.get("tags");
        return tags.get(tags.size() - 1);
    }

    public List<String> listApplications(ApiClient client, Boolean print) throws ApiException {
        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);
        List<String> applicationList = new ArrayList<>();
        LinkedTreeMap result = (LinkedTreeMap) api.listNamespacedCustomObject("nais.io",
                "v1alpha1",
                "q2",
                "applications",
                null,
                null,
                null,
                null);
        ArrayList items = (ArrayList) result.get("items");
        for (Object item : items) {
            LinkedTreeMap app = (LinkedTreeMap) item;
            LinkedTreeMap metadata = (LinkedTreeMap) app.get("metadata");
            String name = (String) metadata.get("name");
            applicationList.add(name);
            if (print) {
                log.info(name);
            }
        }
        return applicationList;
    }

    public boolean applicationExists(ApiClient client, String appName) throws ApiException {
        List<String> applicationList = listApplications(client, false);
        for (String name : applicationList) {
            if (name.equals(appName)) {
                return true;
            }
        }
        return false;
    }

    public void waitForIsAlive(ApiClient client, String appName, IService serviceObject) throws ApiException, InterruptedException {
        log.info("Checking liveness..");
        int num_retries = 0;
        boolean stillDeploying = true;
        while (stillDeploying) {
            try {
                if (applicationIsAlive(serviceObject)) {
                    log.info("It's Alive!");
                    stillDeploying = false;
                }
            } catch (Exception e) { // catching the nullptr here i think?
                if (num_retries < maxRetries) {
                    TimeUnit.SECONDS.sleep(retryDelay);
                    log.info("Waiting for " + appName + " to come alive: " + e);
                } else {
                    log.error("Application" + appName + "failed to come alive. Terminating..");
                    deleteApplication(client, appName);
                }
            }
            num_retries++;
        }
    }


    public boolean applicationIsAlive(IService serviceObject) {
        try {
            return serviceObject.isAlive().equals("1");
        } catch (Exception e) {
            return false;
        }
    }


    public void deleteApplication(ApiClient client, String appName) throws ApiException {
        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);
        Boolean applicationExists = applicationExists(client, appName);
        if (applicationExists) {
            V1DeleteOptions deleteOptions = new V1DeleteOptions();
            try {
                api.deleteNamespacedCustomObject("nais.io",
                        "v1alpha1",
                        "q2",
                        "applications",
                        appName,
                        deleteOptions,
                        null,
                        null,
                        null);
                log.info("Successfully deleted application --> " + appName);
            } catch (JsonSyntaxException e) {
                if (e.getCause() instanceof IllegalStateException) {
                    IllegalStateException ise = (IllegalStateException) e.getCause();
                    if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
                        log.info("Successfully deleted application: " + appName);
                    } else throw e;
                } else throw e;
            }
        } else {
            throw new IllegalArgumentException("No application named: " + appName + " found, unable to delete");
        }
    }
}
