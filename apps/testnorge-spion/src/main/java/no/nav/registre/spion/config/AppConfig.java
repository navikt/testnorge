package no.nav.registre.spion.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;


@Configuration
@ComponentScan
@EnableAutoConfiguration
@Import(ApplicationCoreConfig.class)
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}