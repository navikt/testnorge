package no.nav.registre.syntrest.config;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileReader;
import java.io.IOException;

@Configuration
@Profile("prod")
@Slf4j
public class KubernetesConfig {
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
}

