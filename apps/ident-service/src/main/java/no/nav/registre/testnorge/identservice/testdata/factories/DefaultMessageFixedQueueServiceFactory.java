package no.nav.registre.testnorge.identservice.testdata.factories;


import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;
import no.nav.registre.testnorge.identservice.testdata.environments.FasitApiConsumer;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.ConnectionFactoryFactoryStrategy;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.QueueManagerConnectionFactoryFactoryStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.CHANNEL_POSTFIX;


/**
 * Consumes information from Fasit and produces MessageQueueServices
 */
@Component
@ConditionalOnProperty(prefix = "tps.forvalteren", name = "production.mode", havingValue = "false", matchIfMissing = true)
public class DefaultMessageFixedQueueServiceFactory implements MessageFixedQueueServiceFactory {

    @Autowired
    private FasitApiConsumer fasitClient;

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
    public MessageQueueConsumer createMessageQueueConsumerWithFixedQueueName(String environment, String fixedQueueName) throws JMSException {

        environment = environment.toLowerCase();
        if(environment.contains("d")){
            environment = "u6";
        }

        QueueManager queueManager = fasitClient.getQueueManager(environment);

        ConnectionFactoryFactoryStrategy connectionFactoryFactoryStrategy = new QueueManagerConnectionFactoryFactoryStrategy(queueManager,
                (environment).toUpperCase() + CHANNEL_POSTFIX);

        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionFactoryFactoryStrategy);

        return new MessageQueueConsumer(
                fixedQueueName.toUpperCase(),
                connectionFactory);
    }

}
