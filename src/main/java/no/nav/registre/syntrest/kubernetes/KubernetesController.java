package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Component
public class KubernetesController {

    // Sjekk om application kjører på NAIS
    // // Hvis ikke, spinn opp ny via KubernetesController
    // Sjekk om den er ledig
    // Lås applikasjonen s.a. bare denne innstansen har tilgang
    // Gjør kall på applikasjonen
    // Lås opp applikasjonen igjen, og la andre få tilgang på den.
    // Sjekk om flere skal bruke applikasjonen
    // // Hvis ikke slett applikasjonen


    @Value("${max-alive-retries}")
    private int maxRetries;

    @Value("${alive-retry-delay")
    private int retryDelay;

    public String getApiInterface(String appName) {

        return "";
    }

//    public ApiClient createApiClient() throws IOException {
//        String kubeConfigFilePath = "/var/run/secrets/nais.io/vault/kubeconfig";
//
//        try {
//            KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader(kubeConfigFilePath));
//            return Config.fromConfig(kc);
//        } catch (FileNotFoundException e) {
//            log.error("Could not find kubeconfig file at {}.", kubeConfigFilePath);
//            throw e;
//        }
//    }

}
