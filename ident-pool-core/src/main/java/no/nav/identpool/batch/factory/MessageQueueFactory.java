package no.nav.identpool.batch.factory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.batch.consumers.DefaultMessageQueueConsumer;
import no.nav.identpool.batch.strategy.ConnectionStrategy;

@Component
@RequiredArgsConstructor
public class MessageQueueFactory {

    @Value("${TPS_FORESPORSEL_XML_O_QUEUENAME}")
    private String tpsRequestQueue;

    @Value("${messagequeue.channel.environment}")
    private String channelEnvironment;

    @Value("${messagequeue.consumer.username}")
    private String messageQueueUsername;

    @Value("${messagequeue.consumer.password}")
    private String messageQueuePassword;

    private final ConnectionStrategyFactory connectionStrategyFactory;
    private final ConnectionFactoryFactory connectionFactoryFactory;

    public DefaultMessageQueueConsumer createMessageQueue(String environment) throws JMSException {
        String requestQueue = String.format(tpsRequestQueue, environment.toUpperCase());
        ConnectionStrategy connectionStrategy = connectionStrategyFactory.createConnectionStrategy(channelEnvironment);
        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionStrategy);
        return new DefaultMessageQueueConsumer(requestQueue, connectionFactory, messageQueueUsername, messageQueuePassword);
    }
}
