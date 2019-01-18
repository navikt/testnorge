package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.IOException;

@RestController
@RequestMapping("api/v1")
public class KubernetesHandler extends KubernetesUtils {

    @GetMapping(value = "/test")
    public String testKubernetes(String[] args) throws IOException, ApiException{
        KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader("C:\\nais\\kubeconfigs_naiserator\\config"));
        ApiClient client = Config.fromConfig(kc);

        listApplications(client, true);

        createApplication(client, "");

        return "Alive";
    }
}