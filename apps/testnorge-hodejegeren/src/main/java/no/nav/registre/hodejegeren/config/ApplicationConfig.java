package no.nav.registre.hodejegeren.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Random;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

@Configuration
@EnableAsync
@Import(ApplicationCoreConfig.class)
public class ApplicationConfig {

    @Bean
    public TaskExecutor executor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(100);
        executor.setCorePoolSize(50);
        executor.setThreadNamePrefix("thread-pool-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Random rand() {
        return new Random();
    }
}
