//package no.nav.registre.syntrest.config;
//
//import lombok.RequiredArgsConstructor;
//import no.nav.registre.syntrest.kubernetes.ApplicationManager;
//import no.nav.registre.syntrest.kubernetes.KubernetesController;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//
//@Configuration
//@ComponentScan(basePackageClasses = KubernetesConfig.class)
//@RequiredArgsConstructor
//public class ApplicationManagerConfig {
//
//    private final KubernetesController kubernetesController;
//    private final int EXECUTOR_POOL_SIZE = 4;
//
//
//    @Bean
//    ScheduledExecutorService scheduledExecutorService() {
//        return Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
//    }
//
//    @Bean
//    @DependsOn({"kubernetesController", "scheduledExecutorService"})
//    public ApplicationManager applicationManager() {
//        return new ApplicationManager(kubernetesController, scheduledExecutorService());
//    }
//}
