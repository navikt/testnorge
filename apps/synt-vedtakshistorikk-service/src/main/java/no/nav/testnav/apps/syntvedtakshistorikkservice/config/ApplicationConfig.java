package no.nav.testnav.apps.syntvedtakshistorikkservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class ApplicationConfig {

    private static final int THREADS_COUNT = 10;

    @Bean
    public ExecutorService syntForkJoinPool() {
        return new DelegatingSecurityContextExecutorService(
                new ForkJoinPool(THREADS_COUNT, new SyntForkJoinWorkerThreadFactory(), null, true));
    }
}
