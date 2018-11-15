package no.nav.identpool;

import no.nav.identpool.ajourhold.mq.QueueContext;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import no.nav.identpool.ajourhold.CronJobService;
import no.nav.identpool.ajourhold.mq.factory.ConnectionStrategyFactory;
import no.nav.identpool.ajourhold.mq.factory.MessageQueueFactory;

@Configuration
@ComponentScan(basePackages = "no.nav.identpool")
class ComponentTestConfig {

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
