package no.nav.dolly;

import no.nav.dolly.config.ForkJoinWorkerThreadFactory;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

@SpringBootApplication
@Import({ ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class })
public class ApplicationConfig {

    private static final int THREADS_COUNT = 10;

    @PostConstruct
    public void enableAuthCtxOnSpawnedThreads() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    public ExecutorService dollyForkJoinPool() {
        return new DelegatingSecurityContextExecutorService(new ForkJoinPool(THREADS_COUNT, new ForkJoinWorkerThreadFactory(), null, true));
    }
}