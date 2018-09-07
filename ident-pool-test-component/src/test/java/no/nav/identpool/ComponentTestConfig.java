package no.nav.identpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = "no.nav.identpool")
public class ComponentTestConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
