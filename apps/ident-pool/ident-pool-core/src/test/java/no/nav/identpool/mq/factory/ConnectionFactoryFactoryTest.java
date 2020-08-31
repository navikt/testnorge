package no.nav.identpool.mq.factory;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import no.nav.identpool.test.mockito.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import no.nav.identpool.mq.strategy.ConnectionStrategy;

@ExtendWith(MockitoExtension.class)
class ConnectionFactoryFactoryTest {

    @Spy
    private ConnectionFactoryFactory connectionFactoryFactory;

    @Test
    void testCreateFactory() throws JMSException {

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
