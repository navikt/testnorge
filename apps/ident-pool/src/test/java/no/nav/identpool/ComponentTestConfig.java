package no.nav.identpool;

import no.nav.identpool.ajourhold.CronJobService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = "no.nav.identpool")
public class ComponentTestConfig {

    @MockBean
    CronJobService cronJobService;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
