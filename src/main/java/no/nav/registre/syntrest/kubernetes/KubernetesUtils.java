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

    public void createApplication(ApiClient client, String manifestPath, IService serviceObject) throws InterruptedException, ApiException {
        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);
        Yaml yaml = new Yaml();
        Object manifest = yaml.load(getClass().getResourceAsStream(manifestPath));
        Map<String, String> metadata = (Map)((Map) manifest).get("metadata");
        String appName = metadata.get("name");
        if(!applicationExists(client, appName)){
            try {
                api.createNamespacedCustomObject("nais.io", "v1alpha1", "default", "applications", manifest, null);
                log.info("Application: " + appName + " deployed successfully!");
                log.info("Checking liveness..");
                boolean stillDeploying = true;
                while (stillDeploying) {
                    try {
                        if (serviceObject.isAlive().equals("1")) {
                            log.info("It's Alive!");
                            stillDeploying = false;
                        }
                    } catch (Exception e) {
                        TimeUnit.SECONDS.sleep(2);
                        log.info("Waiting for app to come alive: " + e);
                    }
                }
            } catch (ApiException e) {
                log.info(e.getResponseBody());
            }
        }
    }

    public List<String> listApplications(ApiClient client, Boolean print) throws ApiException {
        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);
        List<String> applicationList = new ArrayList<>();
        LinkedTreeMap result = (LinkedTreeMap) api.listNamespacedCustomObject("nais.io", "v1alpha1", "default", "applications", null, null, null, null);
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

    public void deleteApplication(ApiClient client, String appName) throws ApiException {
        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);
        Boolean applicationExists = applicationExists(client, appName);
        if (applicationExists) {
            V1DeleteOptions deleteOptions = new V1DeleteOptions();
            try {
                api.deleteNamespacedCustomObject("nais.io", "v1alpha1", "default", "applications", appName, deleteOptions, null, null, null);
            } catch (JsonSyntaxException e) {
                if (e.getCause() instanceof IllegalStateException) {
                    IllegalStateException ise = (IllegalStateException) e.getCause();
                    if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
                        log.info("Successfully deleted application --> " + appName);
                    } else throw e;
                } else throw e;
            }
        } else {
            throw new IllegalArgumentException("No application named: " + appName + " found");
        }
    }
}

