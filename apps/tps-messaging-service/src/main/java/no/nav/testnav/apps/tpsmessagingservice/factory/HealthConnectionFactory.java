package no.nav.testnav.apps.tpsmessagingservice.factory;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jms.JmsConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;
import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@Configuration
public class HealthConnectionFactory {

    private static final int UTF_8_WITH_PUA = 1208;

    @Value("${config.mq.preprod.queueManager}")
    private String queueManagerPreprod;
    @Value("${config.mq.preprod.host}")
    private String hostPreprod;
    @Value("${config.mq.preprod.port}")
    private Integer portPreprod;

    @Bean
    public QueueManager queueManager() {
        return new QueueManager(queueManagerPreprod, hostPreprod, portPreprod, "Q1_TESTNAV_TPS_MSG_S");
    }

    @Bean
    public ConnectionFactory connectionFactory(QueueManager queueManager) throws JMSException {

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

        return factory;
    }
}
