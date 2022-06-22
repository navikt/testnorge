package no.nav.testnav.apps.tpsmessagingservice.factory;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jms.JmsConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

import javax.jms.JMSException;

@Configuration
public class HealthConnectionFactory {

    private static final int UTF_8_WITH_PUA = 1208;

    @Value("${config.mq.preprod.queueManager}")
    private String queueManager;
    @Value("${config.mq.preprod.host}")
    private String host;
    @Value("${config.mq.preprod.port}")
    private Integer port;
    @Value("${config.mq.preprod.user}")
    private String username;
    @Value("${config.mq.preprod.password}")
    private String password;

    @Bean
    @Primary
    public UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter() throws JMSException {

        var factory = new MQQueueConnectionFactory();
        factory.setCCSID(UTF_8_WITH_PUA);
        factory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
        factory.setIntProperty(JmsConstants.JMS_IBM_ENCODING, CMQC.MQENC_NATIVE);
        factory.setBooleanProperty(JmsConstants.USER_AUTHENTICATION_MQCSP, true);
        factory.setIntProperty(JmsConstants.JMS_IBM_CHARACTER_SET, UTF_8_WITH_PUA);
        factory.setQueueManager(queueManager);
        factory.setHostName(host);
        factory.setPort(port);
        factory.setChannel("Q1_TESTNAV_TPS_MSG_S");

        var userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
        userCredentialsConnectionFactoryAdapter.setUsername(username);
        userCredentialsConnectionFactoryAdapter.setPassword(password);
        userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(factory);

        return userCredentialsConnectionFactoryAdapter;
    }
}
