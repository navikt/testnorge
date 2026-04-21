package no.nav.testnav.apps.syntvedtakshistorikkservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

@Configuration
public class ApplicationConfig {

    private static final int THREADS_COUNT = 10;

    @Bean
    public ExecutorService syntForkJoinPool() {
        return new ForkJoinPool(THREADS_COUNT, new SyntForkJoinWorkerThreadFactory(), null, true);
    }
}
