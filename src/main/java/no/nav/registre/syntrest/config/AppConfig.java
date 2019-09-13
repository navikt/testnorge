package no.nav.registre.syntrest.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final int EXECUTOR_POOL_SIZE = 4;

    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(EXECUTOR_POOL_SIZE);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}