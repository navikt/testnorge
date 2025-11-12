package no.nav.testnav.pdllagreservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@EnableRetry
@Configuration
@Import(ApplicationCoreConfig.class)
public class ApplicationConfig {

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }
}