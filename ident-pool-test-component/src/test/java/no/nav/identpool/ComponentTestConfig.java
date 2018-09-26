package no.nav.identpool;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import no.nav.identpool.ident.ajourhold.CronJobService;
import no.nav.identpool.ident.ajourhold.mq.QueueContext;
import no.nav.identpool.ident.ajourhold.mq.factory.ConnectionStrategyFactory;
import no.nav.identpool.ident.ajourhold.mq.factory.MessageQueueFactory;

@Configuration
@ComponentScan(basePackages = "no.nav.identpool") class ComponentTestConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @MockBean
    CronJobService cronJobService;

    @MockBean
    ConnectionStrategyFactory connectionStrategyFactory;

    @MockBean
    MessageQueueFactory messageQueueFactory;

    @MockBean
    QueueContext queueContext;
}
