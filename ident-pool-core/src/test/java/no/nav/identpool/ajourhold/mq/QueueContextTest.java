package no.nav.identpool.ajourhold.mq;

import no.nav.identpool.ajourhold.fasit.FasitClient;
import no.nav.identpool.ajourhold.mq.consumer.MessageQueue;
import no.nav.identpool.ajourhold.mq.factory.MessageQueueFactory;
import no.nav.identpool.test.mockito.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueContextTest {

    @Mock
    private FasitClient fasitClient;

    @Mock
    private MessageQueueFactory messageQueueFactory;

    @Mock
    private MessageQueue messageQueue;

    private QueueContextController controller = new QueueContextController();

    private QueueContext queueContext;

    @BeforeEach
    void init() {
        reset(fasitClient, messageQueueFactory, messageQueue);
        queueContext = new QueueContext(fasitClient, messageQueueFactory);
        queueContext.excluded = new String[]{};
        queueContext.order = Collections.emptyList();
    }

    @Test
    void queueContextInit() {
        queueContext.init();
        assertThat(controller.getQueueEnvironContext().getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void queueContextOrdered() throws JMSException {
        when(fasitClient.getAllEnvironments("t", "q")).thenReturn(new ArrayList<>(Arrays.asList("t6", "q9", "t1", "t7", "q8")));
        when(messageQueueFactory.createMessageQueue(anyString())).thenReturn(messageQueue);
        when(messageQueue.ping()).thenReturn(true);

        QueueContext queueContext = new QueueContext(fasitClient, messageQueueFactory);
        queueContext.excluded = new String[]{"t7", "q9"};
        queueContext.order = Arrays.asList("t1", "t6", "t7");
        queueContext.init();
        assertThat(QueueContext.getIncluded(), contains("t1", "t6", "q8"));
        assertThat(QueueContext.getExcluded().size(), is(0));
    }

    @Test
    void queueContextPingError() throws JMSException {
        when(fasitClient.getAllEnvironments("t", "q")).thenReturn(new ArrayList<>(Arrays.asList("t6", "q9", "t1", "t7", "q8")));
        when(messageQueueFactory.createMessageQueue(anyString())).thenReturn(messageQueue);
        when(messageQueue.ping()).thenThrow(new JMSException(""));

        QueueContext queueContext = new QueueContext(fasitClient, messageQueueFactory);
        queueContext.excluded = new String[]{"t7", "q9"};
        queueContext.order = Arrays.asList("t1", "t6", "t7");
        queueContext.init();
        assertThat(QueueContext.getIncluded().size(), is(0));
        assertThat(QueueContext.getExcluded(), containsInAnyOrder("t1", "q8", "t6"));
        assertThat(QueueContext.getExcluded().size(), is(3));
    }
}
