package no.nav.identpool.mq.factory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import no.nav.identpool.fasit.FasitClient;
import no.nav.identpool.fasit.QueueManager;
import no.nav.identpool.mq.strategy.ConnectionStrategy;
import no.nav.identpool.test.mockito.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectionStrategyFactoryTest {

    private static final String environment = "env";
    private static final String environmentIllegalPort = "illegal";

    private static final QueueManager queueManager = new QueueManager("name", "host", "100");
    private static final QueueManager queueManagerIllegalPort = new QueueManager("name", "host", "not a number");

    @Mock
    private FasitClient fasitClient;

    @BeforeEach
    void before() {
        when(fasitClient.getQueueManager(environment)).thenReturn(queueManager);
        when(fasitClient.getQueueManager(environmentIllegalPort)).thenReturn(queueManagerIllegalPort);
    }

    @Test
    void testCreateStrategy() {
        QueueManager queueManager = new QueueManager("name", "host", "100");

        ConnectionStrategy strategy = new ConnectionStrategyFactory(fasitClient).createConnectionStrategy(environment);
        assertThat(strategy.getQueueManager(), equalTo(queueManager.getName()));
        assertThat(strategy.getHostName(), equalTo(queueManager.getHostname()));
        assertThat(strategy.getPort().toString(), equalTo(queueManager.getPort()));
    }

    @Test
    void testIllegalPort() {
        assertThrows(NumberFormatException.class,
                () -> new ConnectionStrategyFactory(fasitClient).createConnectionStrategy(environmentIllegalPort));
    }
}
