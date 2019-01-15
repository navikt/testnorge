/*
package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class KubernetesHandler {

    public void main(String[] args) throws IOException, ApiException{
        Reader fr = new FileReader("C:\\nais\\kubeconfigs\\config");
        ApiClient config = Config.fromConfig(KubeConfig.loadKubeConfig(fr));
        System.out.println(config);

*/
/*        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }*//*

    }
}

*/
