package no.nav.pdl.forvalter.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

@Configuration
@Import({ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class})
public class ApplicationConfig {

    private static final int THREADS_COUNT = 10;

    @PostConstruct
    public void enableAuthCtxOnSpawnedThreads() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    public ExecutorService pdlForkJoinPool() {
        return new DelegatingSecurityContextExecutorService(new ForkJoinPool(THREADS_COUNT, new ForkJoinWorkerThreadFactory(), null, true));
    }
}