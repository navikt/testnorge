package no.nav.registre.testnorge.identservice.testdata.factories;


import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.ConnectionFactoryFactoryStrategy;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.QueueManagerConnectionFactoryFactoryStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.CHANNEL_POSTFIX;

@Component
@ConditionalOnProperty(prefix = "testnorge-ident-service", name = "production.mode", havingValue = "true")
public class ProdMessageQueueServiceFactory implements MessageQueueServiceFactory {

    @Value("${tps.foresporsel.xml.o.queuename}")
    private String tpsRequestQueue;

    @Value("${mqgateway.name}")
    private String queueManagerAlias;

    @Value("${mqgateway.hostname}")
    private String hostname;

    @Value("${mqgateway.port}")
    private String port;


    @Autowired
    private ConnectionFactoryFactory connectionFactoryFactory;

    @Override
    public MessageQueueConsumer createMessageQueueConsumer(String environment, String requestQueueAlias, boolean isQueueName) throws JMSException {
        QueueManager queueManager = new QueueManager(queueManagerAlias, hostname, port, (environment).toUpperCase() + CHANNEL_POSTFIX);

        ConnectionFactoryFactoryStrategy connectionFactoryFactoryStrategy = new QueueManagerConnectionFactoryFactoryStrategy(queueManager,
                (environment).toUpperCase() + CHANNEL_POSTFIX);

        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionFactoryFactoryStrategy);

        return new MessageQueueConsumer(
                tpsRequestQueue,
                connectionFactory
        );
    }

}
