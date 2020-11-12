package no.nav.registre.testnorge.identservice.testdata.factories;

import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.ConnectionFactoryFactoryStrategy;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.QueueManagerConnectionFactoryFactoryStrategy;
import no.nav.registre.testnorge.identservice.testdata.queue.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.CHANNEL_HOSTNAME;
import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.CHANNEL_NAME;
import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.CHANNEL_PORT;
import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.CHANNEL_POSTFIX;
import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.QUEUE_NAME;

/**
 * Consumes information from Fasit and produces MessageQueueServices
 */
@Component
@ConditionalOnProperty(prefix = "testnorge-ident-service", name = "production.mode", havingValue = "false", matchIfMissing = true)
public class DefaultMessageQueueServiceFactory implements MessageQueueServiceFactory {

    @Autowired
    private ConnectionFactoryFactory connectionFactoryFactory;

    /**
     * Instantiates a new MessageQueueConsumer in the specified environment
     *
     * @param environment in which the messages will be exchanged
     * @return A MessageQueueConsumer providing communication with an MQ in the specified environment
     * @throws JMSException
     */
    @Override
       public MessageQueueConsumer createMessageQueueConsumer(String environment, String requestQueueAlias, boolean isQueName) throws JMSException {

        QueueManager queueManager = new QueueManager(CHANNEL_NAME, CHANNEL_HOSTNAME, CHANNEL_PORT, null);
        Queue requestQueue = new Queue(QUEUE_NAME, null);

        ConnectionFactoryFactoryStrategy connectionFactoryFactoryStrategy = new QueueManagerConnectionFactoryFactoryStrategy(queueManager,
                (environment).toUpperCase() + CHANNEL_POSTFIX);

        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionFactoryFactoryStrategy);

        return new MessageQueueConsumer(
                requestQueue.getName(),
                connectionFactory
        );
    }
}
