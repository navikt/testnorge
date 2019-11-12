//package no.nav.registre.syntrest.config;
//
//import io.kubernetes.client.ApiClient;
//import io.kubernetes.client.apis.CustomObjectsApi;
//import io.kubernetes.client.util.Config;
//import io.kubernetes.client.util.KubeConfig;
//import lombok.extern.slf4j.Slf4j;
//import no.nav.registre.syntrest.kubernetes.KubernetesController;
//import org.springframework.beans.factory.BeanCreationException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//
//import java.io.FileReader;
//import java.io.IOException;
//
//@Configuration
//@Slf4j
//public class KubernetesConfig {
//
//    @Value("${kube-config-path}")
//    private String kubeConfigPath;
//
//    @Bean
//    ApiClient apiClient() {
//        try {
//            KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath));
//            kc.setContext("dev-fss");
//            return Config.fromConfig(kc);
//        } catch (IOException e) {
//            String errormsg = String.format("Could not apply configuration from %s", kubeConfigPath);
//            log.error("Could not apply configuration from {}.", kubeConfigPath);
//            throw new BeanCreationException(errormsg, e);
//        }
//    }
//
//    @Bean
//    @DependsOn("apiClient")
//    CustomObjectsApi customObjectsApi() {
//        CustomObjectsApi api = new CustomObjectsApi();
//        api.setApiClient(apiClient());
//        return api;
//    }
//
//    @Value("${isAlive}")
//    private String isAliveUrl;
//    @Value("${docker-image-path}")
//    private String dockerImagePath;
//    @Value("${max-alive-retries}")
//    private int maxRetries;
//    @Value("${alive-retry-delay}")
//    private int retryDelay;
//    @Bean
//    @DependsOn({"restTemplate", "customObjectsApi"})
//    public KubernetesController kubernetesController() {
//        return new KubernetesController(customObjectsApi(),
//               isAliveUrl, dockerImagePath, maxRetries, retryDelay);
//    }
//}
