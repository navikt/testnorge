package no.nav.identpool;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import no.nav.identpool.ajourhold.CronJobService;
import no.nav.identpool.mq.factory.ConnectionStrategyFactory;
import no.nav.identpool.mq.factory.MessageQueueFactory;
import no.nav.identpool.service.support.QueueContext;

@Configuration
@ComponentScan(basePackages = "no.nav.identpool")
public class ComponentTestConfig {

    @MockBean
    CronJobService cronJobService;
    @MockBean
    ConnectionStrategyFactory connectionStrategyFactory;
    @MockBean
    MessageQueueFactory messageQueueFactory;
    @MockBean
    QueueContext queueContext;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
