package no.nav.registre.syntrest.config;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
//import no.nav.registre.syntrest.controllers.domains.AaregController;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.kubernetes.KubernetesController;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Configuration
public class AppConfig {

    private final int EXECUTOR_POOL_SIZE = 4;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ApiClient apiClient() {
        try {
            KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader("${kube-config-path}"));
            return Config.fromConfig(kc);
        } catch (IOException e) {
            log.error("Could not apply configuration from {}.", "${kube-config-path}");
            throw new BeanCreationException("Could not create apiClient from file {}.", "${kube-config-path}");
        }
    }

    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
    }

    @Bean
    @DependsOn({"apiClient", "restTemplate"})
    KubernetesController kubernetesController() {
        return new KubernetesController(restTemplate(), apiClient());
    }

    @Bean
    @DependsOn("kubernetesController")
    ApplicationManager applicationManager() {
        return new ApplicationManager(kubernetesController());
    }

    @Bean
    @Scope(value = "prototype")
    @DependsOn({"applicationManager", "restTemplate", "scheduledExecutorService"})
    SyntConsumer syntConsumer(String appName) {
        return new SyntConsumer(applicationManager(), restTemplate(), scheduledExecutorService(), appName);
    }
}