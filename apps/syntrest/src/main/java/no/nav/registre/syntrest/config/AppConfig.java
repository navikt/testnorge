package no.nav.registre.syntrest.config;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Import(ApplicationCoreConfig.class)
public class AppConfig {

    @Value("${kube-config-path}")
    private String kubeConfigPath;

    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        int executorPoolSize = 4;
        return Executors.newScheduledThreadPool(executorPoolSize);
    }

    @Bean
    UriBuilderFactory uriFactory() {
        return new DefaultUriBuilderFactory();
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
}
