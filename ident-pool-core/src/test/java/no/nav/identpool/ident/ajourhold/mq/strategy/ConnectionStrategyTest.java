package no.nav.identpool.ident.ajourhold.mq.strategy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionStrategyTest {

    @Test
    public void testEqual() {
        ConnectionStrategy connectionStrategy = new ConnectionStrategy("", "", 0, "");
        ConnectionStrategy connectionStrategy2 = new ConnectionStrategy("", "", 0, "");
        assertThat(connectionStrategy.hashCode(), is(connectionStrategy2.hashCode()));
        assertThat(connectionStrategy, is(connectionStrategy2));
    }

    private void testConnectionNotEquals(ConnectionStrategy strategy1, ConnectionStrategy strategy2) {
        assertThat(strategy1.hashCode(), not(is(strategy2.hashCode())));
        assertThat(strategy1, not(is(strategy2)));
    }

    @Test
    public void testNotEqual() {
        ConnectionStrategy connectionStrategy1 = new ConnectionStrategy("", "", 0, "");
        ConnectionStrategy connectionStrategy2 = new ConnectionStrategy("queue", "", 0, "");
        ConnectionStrategy connectionStrategy3 = new ConnectionStrategy("queue", "name", 100, "");
        ConnectionStrategy connectionStrategy4 = new ConnectionStrategy("queue", "name", 100, "channel");
        testConnectionNotEquals(connectionStrategy1, connectionStrategy2);
        testConnectionNotEquals(connectionStrategy2, connectionStrategy3);
        testConnectionNotEquals(connectionStrategy3, connectionStrategy4);
    }
}
