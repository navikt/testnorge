package no.nav.testnav.apps.tpsmessagingservice.factory;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jms.JmsConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.config.CacheConfig;
import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@Slf4j
@Component
public class CachedConnectionFactoryFactory implements ConnectionFactoryFactory {

    private static final int UTF_8_WITH_PUA = 1208;

    @Override
    @Cacheable(value = CacheConfig.CACHE_TPSCONFIG, key = "#queueManager.channel")
    public ConnectionFactory createConnectionFactory(QueueManager queueManager) throws JMSException {

        MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
        factory.setCCSID(UTF_8_WITH_PUA);
        factory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
        factory.setIntProperty(JmsConstants.JMS_IBM_ENCODING, CMQC.MQENC_NATIVE);
        factory.setBooleanProperty(JmsConstants.USER_AUTHENTICATION_MQCSP, true);
        factory.setIntProperty(JmsConstants.JMS_IBM_CHARACTER_SET, UTF_8_WITH_PUA);
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
