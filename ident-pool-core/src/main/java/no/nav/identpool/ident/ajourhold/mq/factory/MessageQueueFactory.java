package no.nav.identpool.ident.ajourhold.mq.factory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.ident.ajourhold.mq.consumer.DefaultMessageQueue;
import no.nav.identpool.ident.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ident.ajourhold.mq.strategy.ConnectionStrategy;

@Component
@RequiredArgsConstructor
public class MessageQueueFactory {

    @Value("${TPS_FORESPORSEL_XML_O_QUEUENAME}")
    private String tpsRequestQueue;

    @Value("${mq.consumer.username}")
    private String messageQueueUsername;

    @Value("${mq.consumer.password}")
    private String messageQueuePassword;

    private final ConnectionStrategyFactory connectionStrategyFactory;
    private final ConnectionFactoryFactory connectionFactoryFactory;

    public MessageQueue createMessageQueue(String environment) throws JMSException {
        environment = environment.toUpperCase();
        String requestQueue = String.format(tpsRequestQueue, environment);
        ConnectionStrategy connectionStrategy = connectionStrategyFactory.createConnectionStrategy(environment);
        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionStrategy, false);
        return new DefaultMessageQueue(requestQueue, connectionFactory, messageQueueUsername, messageQueuePassword);
    }

    public MessageQueue createMessageQueueIgnoreCache(String environment) throws JMSException {
        environment = environment.toUpperCase();
        String requestQueue = String.format(tpsRequestQueue, environment);
        ConnectionStrategy connectionStrategy = connectionStrategyFactory.createConnectionStrategy(environment);
        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionStrategy, true);
        return new DefaultMessageQueue(requestQueue, connectionFactory, messageQueueUsername, messageQueuePassword);
    }
}
