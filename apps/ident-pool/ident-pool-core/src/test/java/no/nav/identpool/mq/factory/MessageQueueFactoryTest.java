package no.nav.identpool.mq.factory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;

import no.nav.identpool.test.mockito.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import no.nav.identpool.mq.strategy.ConnectionStrategy;

@ExtendWith(MockitoExtension.class)
class MessageQueueFactoryTest {

    private static final String environment = "T10";

    @Mock
    private ConnectionStrategyFactory connectionStrategyFactory;

    @Mock
    private ConnectionFactoryFactory connectionFactoryFactory;

    @BeforeEach
    void before() throws JMSException {
        when(connectionStrategyFactory.createConnectionStrategy(environment)).thenReturn(new ConnectionStrategy("", "", 100, ""));
        when(connectionFactoryFactory.createConnectionFactory(any(), anyBoolean())).thenReturn(new MQQueueConnectionFactory());
    }

    @Test
    void testCreateMessageQueue() throws JMSException {
        MessageQueueFactory messageQueueFactory = new MessageQueueFactory(connectionStrategyFactory, connectionFactoryFactory);
        ReflectionTestUtils.setField(messageQueueFactory, "tpsRequestQueue", "%s");
        messageQueueFactory.createMessageQueue(environment);
        verify(connectionStrategyFactory, times(1)).createConnectionStrategy(environment);
        verify(connectionFactoryFactory, times(1)).createConnectionFactory(any(), anyBoolean());
        verify(connectionFactoryFactory, times(0)).createFactory(any());
    }
}
