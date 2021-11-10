package no.nav.testnav.apps.tpsmessagingservice.factory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.config.CacheConfig;
import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;

@Slf4j
@Component
public class CachedConnectionFactoryFactory implements ConnectionFactoryFactory {

    @Override
    @Cacheable(value = CacheConfig.CACHE_TPSCONFIG, key = "#queueManager.channel")
    public ConnectionFactory createConnectionFactory(QueueManager queueManager) throws JMSException {

        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
        factory.setTransportType(1);
        factory.setQueueManager(queueManager.queueManagerName());
        factory.setHostName(queueManager.host());
        factory.setPort(queueManager.port());
        factory.setChannel(queueManager.channel());

        if (log.isInfoEnabled()) {
            log.info(String.format("Creating connection factory '%s@%s:%d' on channel '%s' using transport type '%d'",
                    factory.getQueueManager(),
                    factory.getHostName(),
                    factory.getPort(),
                    factory.getChannel(),
                    factory.getTransportType()));
        }

        return factory;
    }
}
