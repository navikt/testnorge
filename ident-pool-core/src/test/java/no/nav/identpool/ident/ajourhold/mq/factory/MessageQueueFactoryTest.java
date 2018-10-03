package no.nav.identpool.ident.ajourhold.mq.factory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;

import com.ibm.mq.jms.MQQueueConnectionFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import no.nav.identpool.ident.ajourhold.mq.strategy.ConnectionStrategy;

@RunWith(MockitoJUnitRunner.class)
public class MessageQueueFactoryTest {

    private static final String environment = "T10";

    @Mock
    private ConnectionStrategyFactory connectionStrategyFactory;

    @Mock
    private ConnectionFactoryFactory connectionFactoryFactory;

    @Before
    public void before() throws JMSException {
        when(connectionStrategyFactory.createConnectionStrategy(environment)).thenReturn(new ConnectionStrategy("", "", 100, ""));
        when(connectionFactoryFactory.createConnectionFactory(any(), anyBoolean())).thenReturn(new MQQueueConnectionFactory());
    }

    @Test
    public void testCreateMessageQueue() throws JMSException {
        MessageQueueFactory messageQueueFactory = new MessageQueueFactory(connectionStrategyFactory, connectionFactoryFactory);
        ReflectionTestUtils.setField(messageQueueFactory, "tpsRequestQueue", "%s");
        messageQueueFactory.createMessageQueue(environment);
        verify(connectionStrategyFactory, times(1)).createConnectionStrategy(environment);
        verify(connectionFactoryFactory, times(1)).createConnectionFactory(any(), anyBoolean());
        verify(connectionFactoryFactory, times(0)).createFactory(any());
    }
}
