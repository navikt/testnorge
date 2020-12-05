package no.nav.registre.testnorge.identservice.testdata.factories;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import no.nav.registre.testnorge.identservice.testdata.factories.strategies.ConnectionFactoryFactoryStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;


/**
 * A connection factory factory maintaining an internal cache of connection factories.
 * This avoids instantiating new factories for every request, and dramatically improves performance.
 */
@Component
public class CachedConnectionFactoryFactory implements ConnectionFactoryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedConnectionFactoryFactory.class);

    private static final long CACHE_HOURS_TO_LIVE = 6;

    private Cache<String, ConnectionFactory> cache;


    public CachedConnectionFactoryFactory() {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(CACHE_HOURS_TO_LIVE, TimeUnit.HOURS)
                .build();
    }

    @Override
    public ConnectionFactory createConnectionFactory(ConnectionFactoryFactoryStrategy strategy) throws JMSException {
        ConnectionFactory factory = getFactoryForManagerFromCache(strategy);

        if (factory != null) {
            return factory;
        }

        factory = createConnectionFactoryForManager(strategy);

        addFactoryForManagerToCache(factory, strategy);

        return factory;
    }

    /**
     * Create a new connection factory. This is computationally expensive.
     *
     * @param strategy used to configure the connection factory
     * @return a new ConnectionFactory
     * @throws JMSException
     */
    private ConnectionFactory createConnectionFactoryForManager(ConnectionFactoryFactoryStrategy strategy) throws JMSException {
        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();

        Integer transportType   = strategy.getTransportType();
        String hostName         = strategy.getHostName();
        Integer port            = strategy.getPort();
        String queueManagerName = strategy.getName();
        String channel          = strategy.getChannelName();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Creating connection factory '%s@%s:%d' on channel '%s' using transport type '%d'",
                    queueManagerName,
                    hostName,
                    port,
                    channel,
                    transportType));
        }

        factory.setTransportType(transportType);
        factory.setQueueManager(queueManagerName);
        factory.setHostName(hostName);
        factory.setPort(port);
        factory.setChannel(channel);

        return factory;
    }

    /* Cache */

    private void addFactoryForManagerToCache(ConnectionFactory factory, ConnectionFactoryFactoryStrategy strategy) {
        cache.put( getIdentifier(strategy), factory );
    }

    private ConnectionFactory getFactoryForManagerFromCache(ConnectionFactoryFactoryStrategy strategy) {
        return cache.getIfPresent( getIdentifier(strategy) );
    }

    private String getIdentifier(ConnectionFactoryFactoryStrategy strategy) {
        return String.format("%s@%s:%d %s %d", strategy.getName(), strategy.getHostName(), strategy.getPort(), strategy.getChannelName(), strategy.getTransportType());
    }
}
