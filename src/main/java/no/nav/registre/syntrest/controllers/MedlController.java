package no.nav.registre.syntrest.controllers;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.MedlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/v1")
public class MedlController extends KubernetesUtils {

    @Autowired
    private MedlService medlService;

    @GetMapping(value = "/generateMedl/{num_to_generate}")
    public List<Map<String, String>> generateMedl(@PathVariable int num_to_generate) throws InterruptedException, ExecutionException, IOException, ApiException {

        KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader("C:\\nais\\kubeconfigs\\config"));
        ApiClient client = Config.fromConfig(kc);

        System.out.println("Creating application..");
        createApplication(client, "C:\\Users\\E154653\\Desktop\\synt\\syntrest\\src\\main\\java\\no\\nav\\registre\\syntrest\\config\\synthdata-medl.yaml");

        System.out.println("Checking liveness..");
        boolean stillDeploying = true;
        while (stillDeploying){
            try{
                if (medlService.isAlive().equals("1")){
                    System.out.println("It's Alive!");
                    stillDeploying = false;
                }
            } catch (HttpClientErrorException | HttpServerErrorException e){
                TimeUnit.SECONDS.sleep(1);
            }
        }

        System.out.println("Requesting stuff..");
        CompletableFuture<List<Map<String, String>>> result = medlService.generateMedlFromNAIS(num_to_generate);
        List<Map<String, String>> synData = result.get();

        System.out.println("Deleting application..");
        deleteApplication(client, "synthdata-medl");

        return synData;
    }
}
