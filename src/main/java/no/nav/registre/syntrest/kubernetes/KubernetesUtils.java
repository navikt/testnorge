package no.nav.registre.syntrest.kubernetes;

import com.google.gson.*;


import com.google.gson.internal.LinkedHashTreeMap;
import com.google.gson.internal.LinkedTreeMap;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.JSON;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KubernetesUtils {

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


    public void deleteDeployment(ApiClient client, String appName)throws ApiException{

        ExtensionsV1beta1Api api = new ExtensionsV1beta1Api();
        api.setApiClient(client);

        ExtensionsV1beta1DeploymentList deploymentList = listSynthDeployments(client);

        Boolean deploymentExists = false;
        for (ExtensionsV1beta1Deployment deployment : deploymentList.getItems()){
            if (deployment.getMetadata().getName().equals(appName)){
                deploymentExists = true;
            }
        }

        if (deploymentExists){
            V1DeleteOptions deleteOptions = new V1DeleteOptions();

            try {
                V1Status status = api.deleteNamespacedDeployment(appName, "default", deleteOptions, null, null, null, null);
            }catch (JsonSyntaxException e) {
                if (e.getCause() instanceof IllegalStateException) {
                    IllegalStateException ise = (IllegalStateException) e.getCause();
                    if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")){
                        System.out.println("Successfully deleted deployment --> " + appName);
                    }
                    else throw e;
                }
                else throw e;
            }
        } else{
            throw new IllegalArgumentException("No deployment named:" + appName + "found");
        }
    }

    public void createDeployment(ApiClient client, String appName)throws ApiException{


        //TODO: CustomObjectsApi  <-------

        ExtensionsV1beta1Api api = new ExtensionsV1beta1Api();
        api.setApiClient(client);

        ExtensionsV1beta1DeploymentSpec deploymentSpec = new ExtensionsV1beta1DeploymentSpec();
        deploymentSpec.setReplicas(1);


        V1ObjectMeta objectMeta = new V1ObjectMeta();
        objectMeta.setName(appName);
        objectMeta.setNamespace("default");
        Map<String, String> labels = new HashMap<String, String>();
        labels.put("team", "teamsynt");
        objectMeta.setLabels(labels);

        ExtensionsV1beta1Deployment deployment = new ExtensionsV1beta1Deployment();
        deployment.setSpec(deploymentSpec);
        deployment.setKind("Application");
        deployment.setMetadata(objectMeta);

        api.createNamespacedDeployment("default", deployment, null);

    }


    public void createApplication(ApiClient client, String manifestPath)throws ApiException, FileNotFoundException {

        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);

        Object jsonObject = convertToJson(new FileReader(manifestPath));

        try{
            api.createNamespacedCustomObject("nais.io", "v1alpha1", "default", "applications", jsonObject, null);
        }catch (ApiException e){
            System.out.println(e.getResponseBody());
        }

    }


    private JSONObject convertToJson(Reader yamlReader){
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(yamlReader);
        return new JSONObject(map);
    }


    public void listApplications(ApiClient client)throws ApiException{

        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(client);

        LinkedTreeMap result = (LinkedTreeMap) api.listNamespacedCustomObject("nais.io", "v1alpha1", "default", "applications", null, null, null, null);
        ArrayList items = (ArrayList) result.get("items");

        for (Object item : items){
            LinkedTreeMap app = (LinkedTreeMap) item;
            LinkedTreeMap metadata = (LinkedTreeMap) app.get("metadata");
            System.out.println(metadata.get("name"));
        }

    }


}

