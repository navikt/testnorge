package no.nav.testnav.apps.syntvedtakshistorikkservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

@Configuration
@Import({ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
        InsecureJwtServerToServerConfiguration.class
})
public class ApplicationConfig {

    private static final int THREADS_COUNT = 10;

    @PostConstruct
    public void enableAuthCtxOnSpawnedThreads() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    public ExecutorService syntForkJoinPool() {
        return new DelegatingSecurityContextExecutorService(
                new ForkJoinPool(THREADS_COUNT, new SyntForkJoinWorkerThreadFactory(), null, true));
    }
}
