package no.nav.identpool.ajourhold.mq.factory;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import no.nav.identpool.ajourhold.mq.strategy.ConnectionStrategy;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionFactoryFactoryTest {

    @Spy
    private ConnectionFactoryFactory connectionFactoryFactory;

    @Test
    public void testCreateFactory() throws JMSException {

        ConnectionStrategy connectionStrategy = new ConnectionStrategy("", "", 0, "");

        doReturn(new MQQueueConnectionFactory()).when(connectionFactoryFactory).createFactory(any(ConnectionStrategy.class));
        ConnectionFactory connectionFactory = connectionFactoryFactory.createConnectionFactory(connectionStrategy, false);

        doReturn(new MQQueueConnectionFactory()).when(connectionFactoryFactory).createFactory(any(ConnectionStrategy.class));
        ConnectionFactory connectionFactoryCache = connectionFactoryFactory.createConnectionFactory(connectionStrategy, false);
        ConnectionFactory connectionFactoryIgnoreCache = connectionFactoryFactory.createConnectionFactory(connectionStrategy, true);

        verify(connectionFactoryFactory, times(2)).createFactory(connectionStrategy);
        assertSame(connectionFactory, connectionFactoryCache);
        assertNotSame(connectionFactory, connectionFactoryIgnoreCache);
    }
}
