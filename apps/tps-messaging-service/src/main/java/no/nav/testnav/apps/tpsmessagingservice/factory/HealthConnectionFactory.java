package no.nav.testnav.apps.tpsmessagingservice.factory;

import jakarta.jms.JMSException;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

@Configuration
@RequiredArgsConstructor
public class HealthConnectionFactory {

    @Value("${config.mq.preprod.queueManager}")
    private String queueManagerName;
    @Value("${config.mq.preprod.host}")
    private String host;
    @Value("${config.mq.preprod.port}")
    private Integer port;
    @Value("${config.mq.preprod.user}")
    private String username;
    @Value("${config.mq.preprod.password}")
    private String password;

    private final CachedConnectionFactoryFactory factoryFactory;

    @Bean
    @Primary
    public UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter()
            throws JMSException {
        var factory = factoryFactory.createConnectionFactory(new QueueManager(queueManagerName, host, port, "Q1_TESTNAV_TPS_MSG_S"));
        var userCredentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
        userCredentialsConnectionFactoryAdapter.setUsername(username);
        userCredentialsConnectionFactoryAdapter.setPassword(password);
        userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(factory);
        return userCredentialsConnectionFactoryAdapter;
    }
}
