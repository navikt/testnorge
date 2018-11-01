package no.nav.identpool.ajourhold.mq.factory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.freg.fasit.utils.domain.QueueManager;
import no.nav.identpool.ajourhold.fasit.FasitClient;
import no.nav.identpool.ajourhold.mq.strategy.ConnectionStrategy;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionStrategyFactoryTest {

    private static final String environment = "env";
    private static final String environmentIllegalPort = "illegal";

    private static final QueueManager queueManager = new QueueManager("name", "host", "100");
    private static final QueueManager queueManagerIllegalPort = new QueueManager("name", "host", "not a number");

    @Mock
    private FasitClient fasitClient;

    @Before
    public void before() {
        when(fasitClient.getQueueManager(environment)).thenReturn(queueManager);
        when(fasitClient.getQueueManager(environmentIllegalPort)).thenReturn(queueManagerIllegalPort);
    }

    @Test
    public void testCreateStrategy() {
        QueueManager queueManager = new QueueManager("name", "host", "100");

        ConnectionStrategy strategy = new ConnectionStrategyFactory(fasitClient).createConnectionStrategy(environment);
        assertThat(strategy.getQueueManager(), equalTo(queueManager.getName()));
        assertThat(strategy.getHostName(), equalTo(queueManager.getHostname()));
        assertThat(strategy.getPort().toString(), equalTo(queueManager.getPort()));
    }

    @Test(expected = NumberFormatException.class)
    public void testIllegalPort() {
        new ConnectionStrategyFactory(fasitClient).createConnectionStrategy(environmentIllegalPort);
    }
}
