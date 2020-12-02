package no.nav.identpool.service.support;

import no.nav.identpool.fasit.FasitClient;
import no.nav.identpool.mq.consumer.MessageQueue;
import no.nav.identpool.mq.factory.MessageQueueFactory;
import no.nav.identpool.test.mockito.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class QueueContextTest {

    @Mock
    private FasitClient fasitClient;

    @Mock
    private MessageQueueFactory messageQueueFactory;

    @Mock
    private MessageQueue messageQueue;

    @BeforeEach
    void init() {
        reset(fasitClient, messageQueueFactory, messageQueue);
    }

    @Test
    void queueContextOrdered() throws JMSException {
        when(fasitClient.getAllEnvironments("t", "q")).thenReturn(new ArrayList<>(Arrays.asList("t6", "q9", "t1", "t7", "q8")));
        when(messageQueueFactory.createMessageQueue(anyString())).thenReturn(messageQueue);

        createQueueContext();
        assertThat(QueueContext.getSuccessfulEnvs(), contains("t1", "t6", "q8"));
        assertThat(QueueContext.getFailedEnvs().size(), is(0));
    }

    @Test
    void queueContextPingError() throws JMSException {
        when(fasitClient.getAllEnvironments("t", "q")).thenReturn(new ArrayList<>(Arrays.asList("t6", "q9", "t1", "t7", "q8")));
        when(messageQueueFactory.createMessageQueue(anyString())).thenReturn(messageQueue);
        doThrow(new JMSException("")).when(messageQueue).ping();

        createQueueContext();

        assertThat(QueueContext.getSuccessfulEnvs().size(), is(0));
        assertThat(QueueContext.getFailedEnvs().size(), is(3));
        assertThat(QueueContext.getFailedEnvs(), containsInAnyOrder("t1", "q8", "t6"));
    }

    private void createQueueContext() {
        QueueContext queueContext = new QueueContext(fasitClient, messageQueueFactory);
        ReflectionTestUtils.setField(queueContext, "excluded", Arrays.asList("t7", "q9"));
        queueContext.init();
    }
}
