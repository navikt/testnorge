package no.nav.registre.syntrest.kubernetes;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class KubernetesUtils {

    //TODO: save kubeconfig to vault and reference it here
    @Value("${/var/run/secrets/nais.io/vault/kubeconfig}")
    private String kubeConfig;

    public ApiClient createApiClient()throws IOException{
        KubeConfig kc = KubeConfig.loadKubeConfig(new StringReader(kubeConfig));
        ApiClient client = Config.fromConfig(kc);
        log.info("successfully loaded kubeconfig!");
        return client;
    }


    public ExtensionsV1beta1DeploymentList listSynthDeployments(ApiClient client)throws ApiException{

        ExtensionsV1beta1Api api = new ExtensionsV1beta1Api();
        api.setApiClient(client);

        ExtensionsV1beta1DeploymentList deploymentList = api.listNamespacedDeployment("default", null, null, null, null, null, null, null, null, null);

        for (ExtensionsV1beta1Deployment deployment : deploymentList.getItems()){
            if (deployment.getMetadata().getName().startsWith("synthdata")){
                System.out.println(deployment.getMetadata().getName());
            }
        }

        return deploymentList;
    }


    public void createApplication(ApiClient client, String manifestPath)throws FileNotFoundException {

        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);

        Object jsonObject = convertToJson(new FileReader(manifestPath));

        try{
            api.createNamespacedCustomObject("nais.io", "v1alpha1", "default", "applications", jsonObject, null);
            Object metadata = ((Map) jsonObject).get("metadata");
            String appName = (String)((Map) metadata).get("name");
            System.out.println("Application: " + appName + " deployed successfully!");
        }catch (ApiException e){
            System.out.println(e.getResponseBody());
        }
    }


    private Map<String, Object> convertToJson(Reader yamlReader){
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(yamlReader);
        return map;
    }


    public List<String> listApplications(ApiClient client, Boolean print)throws ApiException{

        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);

        List<String> applicationList = new ArrayList<>();

        LinkedTreeMap result = (LinkedTreeMap) api.listNamespacedCustomObject("nais.io", "v1alpha1", "default", "applications", null, null, null, null);
        ArrayList items = (ArrayList) result.get("items");

        for (Object item : items){
            LinkedTreeMap app = (LinkedTreeMap) item;
            LinkedTreeMap metadata = (LinkedTreeMap) app.get("metadata");
            String name = (String) metadata.get("name");
            applicationList.add(name);
            if (print){ System.out.println(name); }
        }

        return applicationList;
    }


    public boolean applicationExists(ApiClient client, String appName)throws ApiException{

        List<String> applicationList = listApplications(client, false);

        for (String name : applicationList){
            if (name.equals(appName)){
                return true;
            }
        }
        return false;
    }


    public void deleteApplication(ApiClient client, String appName)throws ApiException{

        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);

        Boolean applicationExists = applicationExists(client, appName);

        if (applicationExists){
            V1DeleteOptions deleteOptions = new V1DeleteOptions();

            try {
                api.deleteNamespacedCustomObject("nais.io", "v1alpha1", "default", "applications", appName, deleteOptions, null, null, null);
            }catch (JsonSyntaxException e) {
                if (e.getCause() instanceof IllegalStateException) {
                    IllegalStateException ise = (IllegalStateException) e.getCause();
                    if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")){
                        System.out.println("Successfully deleted application --> " + appName);
                    }
                    else throw e;
                }
                else throw e;
            }
        } else{
            throw new IllegalArgumentException("No application named: " + appName + " found");
        }

    }

}

