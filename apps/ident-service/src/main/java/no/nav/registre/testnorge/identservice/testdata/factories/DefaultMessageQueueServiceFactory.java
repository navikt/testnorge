package no.nav.registre.testnorge.identservice.testdata.factories;

import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.ConnectionFactoryFactoryStrategy;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.QueueManagerConnectionFactoryFactoryStrategy;
import no.nav.registre.testnorge.identservice.testdata.fasit.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.CHANNEL_POSTFIX;

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

        QueueManager queueManager = new QueueManager("MQLCLIENT01", "b27apvl057.preprod.local", "1413", null);
        Queue requestQueue        = new Queue("QA.Q2_411.TPS_FORESPORSEL_XML_O", null);

        ConnectionFactoryFactoryStrategy connectionFactoryFactoryStrategy = new QueueManagerConnectionFactoryFactoryStrategy(queueManager,
                (environment).toUpperCase() + CHANNEL_POSTFIX);

        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionFactoryFactoryStrategy);

        return new MessageQueueConsumer(
                requestQueue.getName(),
                connectionFactory
        );
    }
}
