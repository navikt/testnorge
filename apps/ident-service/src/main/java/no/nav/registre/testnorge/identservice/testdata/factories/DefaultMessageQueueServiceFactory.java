package no.nav.registre.testnorge.identservice.testdata.factories;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.ConnectionFactoryFactoryStrategy;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.QueueManagerConnectionFactoryFactoryStrategy;
import no.nav.registre.testnorge.identservice.testdata.queue.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * Consumes information from Fasit and produces MessageQueueServices
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "testnorge-ident-service", name = "production.mode", havingValue = "false", matchIfMissing = true)
public class DefaultMessageQueueServiceFactory implements MessageQueueServiceFactory {

    private final ConnectionFactoryFactory connectionFactoryFactory;

    @Value("${mq.username}")
    private String username;
    @Value("${mq.password}")
    private String password;

    @Value("${mq.channel.postfix}")
    private String channelPostfix;
    @Value("${mq.channel.name}")
    private String channelName;
    @Value("${mq.channel.hostname}")
    private String channelHostname;
    @Value("${mq.channel.port}")
    private String channelPort;

    @Value("${mq.queue.name}")
    private String queueName;

    /**
     * Instantiates a new MessageQueueConsumer in the specified environment
     *
     * @param environment in which the messages will be exchanged
     * @return A MessageQueueConsumer providing communication with an MQ in the specified environment
     * @throws JMSException
     */
    @Override
    public MessageQueueConsumer createMessageQueueConsumer(String environment, String requestQueueAlias, boolean isQueName) throws JMSException {

        QueueManager queueManager = new QueueManager(channelName, channelHostname, channelPort, null);
        Queue requestQueue = new Queue(queueName, null);

        ConnectionFactoryFactoryStrategy connectionFactoryFactoryStrategy = new QueueManagerConnectionFactoryFactoryStrategy(queueManager,
                (environment).toUpperCase() + channelPostfix);

        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionFactoryFactoryStrategy);

        return new MessageQueueConsumer(
                requestQueue.getName(),
                connectionFactory,
                username,
                password
        );
    }
}
