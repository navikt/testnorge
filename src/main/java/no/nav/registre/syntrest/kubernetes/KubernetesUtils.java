package no.nav.registre.syntrest.kubernetes;

import com.google.gson.JsonSyntaxException;
import com.google.protobuf.Api;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.BatchV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.proto.V1beta1Extensions;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KubernetesUtils {

    public void listJobs(ApiClient client)throws ApiException, IOException {

        BatchV1Api api = new BatchV1Api();
        api.setApiClient(client);

        V1JobList list = api.listJobForAllNamespaces(null, null,null,null,null,null,null,null,null);
        for (V1Job job : list.getItems()) {
            System.out.println(job.getMetadata().getName());
        }
    }


    public void deletePod(ApiClient client, String appName)throws ApiException {

        CoreV1Api api = new CoreV1Api();
        api.setApiClient(client);

        String podName = "";
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            if (item.getMetadata().getName().startsWith("synthdata")){
                System.out.println(item.getMetadata().getName());
            }
            if (item.getMetadata().getName().startsWith(appName)){
                podName = item.getMetadata().getName();
            }
        }

        V1DeleteOptions deleteOptions = new V1DeleteOptions();

        try {
            api.deleteNamespacedPod(podName, "default", deleteOptions, null, null, null, null);
        }
        catch (JsonSyntaxException e) {
            if (e.getCause() instanceof IllegalStateException) {
                IllegalStateException ise = (IllegalStateException) e.getCause();
                if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")){
                    System.out.println("Deleted pod: " + podName);
                }
                else throw e;
            }
            else throw e;
        }
    }


    public V1Job createJob(String appName)throws ApiException{

        //Specify image
        //String imageName = "docker.adeo.no:5000/registre/synthdata-medl";
        String imageName = "docker.adeo.no:5000/arena-nais-simple-demo:0.0.2-SNAPSHOT";

        //Create container
        V1Container container = new V1Container();
        container.setImage(imageName);

        //Create container list
        List<V1Container> containerList = Arrays.asList(container);

        //Create PodSpec
        V1PodSpec podSpec = new V1PodSpec();
        podSpec.setInitContainers(containerList);

        //Create PodTemplateSpec
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec();
        podTemplateSpec.setSpec(podSpec);

        //Create JobSpec
        V1JobSpec jobSpec = new V1JobSpec();
        jobSpec.setTemplate(podTemplateSpec);

        //Create Job Metadata
        V1ObjectMeta objectMeta = new V1ObjectMeta();
        objectMeta.setName("job-test");

        //Create Job
        V1Job job = new V1Job();
        job.setSpec(jobSpec);
        job.setMetadata(objectMeta);

        return job;
    }


    public void startJob(ApiClient client, V1Job job)throws ApiException{

        BatchV1Api api = new BatchV1Api();
        api.setApiClient(client);

        //TODO: make this async?
        try {
            V1Job response = api.createNamespacedJob("default", job, null);
            System.out.println("Running job:" + job.getMetadata().getName());
        }
        catch (ApiException e){
            System.out.println("Job:" + job.getMetadata().getName() + "FAILED");
            System.out.println(e.getCode());
            System.out.println(e.getResponseBody());
        }
    }


    public void deleteJob(ApiClient client, String name)throws ApiException{

        BatchV1Api api = new BatchV1Api();
        api.setApiClient(client);

        V1DeleteOptions deleteOptions = new V1DeleteOptions();

        try {
            V1Status status = api.deleteNamespacedJob(name, "default", deleteOptions, null, null, null, null);
        }
        catch (ApiException e){
            System.out.println(e.getCode());
            System.out.println(e.getResponseBody());
        }


    }

}
