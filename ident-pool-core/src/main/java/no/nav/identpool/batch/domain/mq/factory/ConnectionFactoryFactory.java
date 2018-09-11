package no.nav.identpool.batch.domain.mq.factory;


import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import lombok.extern.slf4j.Slf4j;

import no.nav.identpool.batch.domain.mq.strategy.ConnectionStrategy;

/**
 * A connection factory factory maintaining an internal cache of connection factories.
 * This avoids instantiating new factories for every request, and dramatically improves performance.
 */
@Component
@Slf4j
public class ConnectionFactoryFactory {

    private static final long CACHE_HOURS_TO_LIVE = 6;

    private static Cache<ConnectionStrategy, ConnectionFactory> cache =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(CACHE_HOURS_TO_LIVE, TimeUnit.HOURS)
                    .build();

    /**
     * Create a new connection factory. This is computationally expensive.
     *
     * @param strategy used to configure the connection factory
     * @return a new ConnectionFactory
     * @throws JMSException
     */
    ConnectionFactory createConnectionFactory(ConnectionStrategy strategy) throws JMSException {
        return createConnectionFactory(strategy, false);
    }

    ConnectionFactory createConnectionFactoryIgnoreCache(ConnectionStrategy strategy) throws JMSException {
        return createConnectionFactory(strategy, true);
    }

    private ConnectionFactory createConnectionFactory(ConnectionStrategy strategy, boolean ignoreCache) throws JMSException {
        if (!ignoreCache) {
            ConnectionFactory connectionFactory = cache.getIfPresent(strategy);
            if (connectionFactory != null) {
                return connectionFactory;
            }
        }
        log.info(String.format("Creating connection factory '%s@%s:%d' on channel '%s' using transport type '%d'",
                strategy.getQueueManager(),
                strategy.getHostName(),
                strategy.getPort(),
                strategy.getChannel(),
                strategy.getTransportType()));

        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();

        factory.setTransportType(strategy.getTransportType());
        factory.setQueueManager(strategy.getQueueManager());
        factory.setHostName(strategy.getHostName());
        factory.setPort(strategy.getPort());
        factory.setChannel(strategy.getChannel());

        if (ignoreCache) {
            cache.invalidate(factory);
        }
        cache.put(strategy, factory);

        return factory;
    }
}
