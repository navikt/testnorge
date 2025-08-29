package no.nav.dolly.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.core.context.SecurityContextHolder;

@Getter
@Configuration
@EnableRetry
@Import({
        ApplicationCoreConfig.class,
        CoreConfig.class
})
public class ApplicationConfig {

    @Value("${dolly.client.general.timeout}")
    private Long clientTimeout;

    @PostConstruct
    public void enableAuthCtxOnSpawnedThreads() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
}