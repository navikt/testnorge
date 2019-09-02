package no.nav.registre.syntrest.config;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
//import no.nav.registre.syntrest.controllers.domains.AaregController;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.kubernetes.KubernetesController;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Configuration
//@Import({ AaregController.class })//MeldekortController.class, EIAController.class, InntektController.class, EIAController.class, MedlController.class})
public class AppConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    Validation validation() {
        return new Validation();
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
    KubernetesController kubernetesController() {
        return new KubernetesController(restTemplate(), apiClient());
    }

    @Bean
    ApplicationManager applicationManager() {
        return new ApplicationManager(kubernetesController());
    }
}