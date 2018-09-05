package no.nav.identpool.batch.factory;


import com.ibm.mq.jms.MQQueueConnectionFactory;

import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import no.nav.identpool.batch.strategy.ConnectionStrategy;

/**
 * A connection factory factory maintaining an internal cache of connection factories.
 * This avoids instantiating new factories for every request, and dramatically improves performance.
 */
@Component
public class ConnectionFactoryFactory {

    /**
     * Create a new connection factory. This is computationally expensive.
     *
     * @param strategy used to configure the connection factory
     * @return a new ConnectionFactory
     * @throws JMSException
     */
    ConnectionFactory createConnectionFactory(ConnectionStrategy strategy) throws JMSException {

        System.out.println( String.format("Creating connection factory '%s@%s:%d' on channel '%s' using transport type '%d'",
                strategy.getQueueManager(),
                strategy.getHostName(),
                strategy.getPort(),
                strategy.getChannel(),
                strategy.getTransportType()) );

        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();

        factory.setTransportType(strategy.getTransportType());
        factory.setQueueManager(strategy.getQueueManager());
        factory.setHostName(strategy.getHostName());
        factory.setPort(strategy.getPort());
        factory.setChannel(strategy.getChannel());


        return factory;
    }
}
