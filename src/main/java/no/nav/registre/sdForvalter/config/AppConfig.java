package no.nav.registre.sdForvalter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;

import no.nav.registre.testnorge.common.config.LoggableDispatcherServlet;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "no.nav.registre.sdForvalter.database.repository")
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new LoggableDispatcherServlet();
    }
}
