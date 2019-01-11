package no.nav.identpool.mq.factory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.mq.consumer.DefaultMessageQueue;
import no.nav.identpool.mq.consumer.MessageQueue;
import no.nav.identpool.mq.strategy.ConnectionStrategy;

@Component
@RequiredArgsConstructor
public class MessageQueueFactory {

    private final ConnectionStrategyFactory connectionStrategyFactory;
    private final ConnectionFactoryFactory connectionFactoryFactory;

    @Value("${TPS_FORESPORSEL_XML_O_QUEUENAME}")
    private String tpsRequestQueue;

    @Value("${mq.consumer.username}")
    private String messageQueueUsername;

    @Value("${mq.consumer.password}")
    private String messageQueuePassword;

    public MessageQueue createMessageQueue(String environment) throws JMSException {
        return getMessageQueue(environment, false);
    }

    public MessageQueue createMessageQueueIgnoreCache(String environment) throws JMSException {
        return getMessageQueue(environment, true);
    }

    private MessageQueue getMessageQueue(String environment, boolean ignoreCache) throws JMSException {
        String requestQueue = String.format(tpsRequestQueue, environment.toUpperCase());
        ConnectionStrategy connectionStrategy = connectionStrategyFactory.createConnectionStrategy(environment.toUpperCase());
        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionStrategy, ignoreCache);
        return new DefaultMessageQueue(requestQueue, connectionFactory, messageQueueUsername, messageQueuePassword);
    }
}
