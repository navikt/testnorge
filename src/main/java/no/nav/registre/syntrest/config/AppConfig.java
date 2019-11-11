package no.nav.registre.syntrest.config;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.kubernetes.KubernetesController;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@Slf4j
public class AppConfig {

    @Value("${kube-config-path}")
    private String kubeConfigPath;
    private final int EXECUTOR_POOL_SIZE = 4;

    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ApiClient apiClient() {
        try {
            KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath));
            kc.setContext("dev-fss");
            return Config.fromConfig(kc);
        } catch (IOException e) {
            String errormsg = String.format("Could not apply configuration from %s", kubeConfigPath);
            log.error("Could not apply configuration from {}.", kubeConfigPath);
            throw new BeanCreationException(errormsg, e);
        }
    }

    @Bean
    @DependsOn("apiClient")
    CustomObjectsApi customObjectsApi() {
        CustomObjectsApi api = new CustomObjectsApi();
        api.setApiClient(apiClient());
        return api;
    }

    @Value("${isAlive}")
    private String isAliveUrl;
    @Value("${docker-image-path}")
    private String dockerImagePath;
    @Value("${max-alive-retries}")
    private int maxRetries;
    @Value("${alive-retry-delay}")
    private int retryDelay;
    @Bean
    @DependsOn({"restTemplate", "customObjectsApi"})
    public KubernetesController kubernetesController() {
        return new KubernetesController(restTemplate(), customObjectsApi(),
               isAliveUrl, dockerImagePath, maxRetries, retryDelay);
    }

    @Bean
    @DependsOn({"kubernetesController", "scheduledExecutorService"})
    public ApplicationManager applicationManager() {
        return new ApplicationManager(kubernetesController(), scheduledExecutorService());
    }


    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer aaregConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-aareg");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer aapConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-arena-aap");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer bisysConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-arena-bisys");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer instConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-inst");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer medlConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-medl");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer meldekortConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-arena-meldekort");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer navConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-nav");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer poppConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-popp");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer samConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-sam");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer inntektConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-inntekt");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer tpConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-tp");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer tpsConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-tps");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer frikortConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-frikort");
    }

    @Bean
    @DependsOn({"applicationManager", "restTemplate"})
    SyntConsumer eiaConsumer() {
        return new SyntConsumer(applicationManager(), restTemplate(), "synthdata-eia");
    }

//    @Bean
//    @DependsOn({"applicationManager", "restTemplate"})
//    SyntConsumer Consumer() {
//        return new SyntConsumer(applicationManager(), restTemplate(), );
//    }
}
