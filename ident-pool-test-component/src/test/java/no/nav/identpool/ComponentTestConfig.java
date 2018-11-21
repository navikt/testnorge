package no.nav.identpool;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import no.nav.identpool.ajourhold.mq.factory.ConnectionStrategyFactory;
import no.nav.identpool.ajourhold.mq.factory.MessageQueueFactory;
import no.nav.identpool.ajourhold.mq.QueueContext;

@Configuration
@ComponentScan(basePackages = "no.nav.identpool")
class ComponentTestConfig {

    @MockBean
    ConnectionStrategyFactory connectionStrategyFactory;
    @MockBean
    MessageQueueFactory messageQueueFactory;
    @MockBean
    QueueContext queueContext;
}
