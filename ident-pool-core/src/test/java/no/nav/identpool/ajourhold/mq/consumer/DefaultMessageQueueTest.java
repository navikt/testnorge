package no.nav.identpool.ajourhold.mq.consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;

import javax.jms.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMessageQueueTest {

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private Connection connection;

    @Mock
    private Session session;

    @Mock
    private Queue queue;

    @Mock
    private TemporaryQueue temporaryQueue;

    @Mock
    private TextMessage textMessage;

    @Mock
    private MessageProducer messageProducer;

    @Mock
    private MessageConsumer messageConsumer;

    private DefaultMessageQueue messageQueue;

    @Before
    public void init() throws JMSException {
        messageQueue = new DefaultMessageQueue("", connectionFactory, "", "");
        when(connectionFactory.createConnection("", "")).thenReturn(connection);
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createQueue("")).thenReturn(queue);
        when(session.createTemporaryQueue()).thenReturn(temporaryQueue);
        when(session.createProducer(eq(queue))).thenReturn(messageProducer);
        when(session.createTextMessage(anyString())).thenReturn(textMessage);
        doNothing().when(messageProducer).send(textMessage);
        when(textMessage.getJMSMessageID()).thenReturn("");
        when(session.createConsumer(eq(temporaryQueue), anyString())).thenReturn(messageConsumer);
        when(messageConsumer.receive(anyLong())).thenReturn(textMessage);
        when(textMessage.getText()).thenReturn("PING!");
    }

    @Test
    public void ping() throws JMSException {
        assertThat(messageQueue.ping(), is(Boolean.TRUE));
    }

    @Test
    public void send() throws JMSException {
        assertThat(messageQueue.sendMessage(""), is("PING!"));
    }

    @Test(expected = JMSException.class)
    public void connectionError() throws JMSException {
        try {
            when(connectionFactory.createConnection("", "")).thenThrow(new JMSException("message"));
            messageQueue.sendMessage("");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("message"));
            verify(connectionFactory, times(4)).createConnection("", "");
            throw e;
        }
    }

    @Test(expected = JMSException.class)
    public void sessionError() throws JMSException {
        try {
            when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenThrow(new JMSException("message"));
            messageQueue.sendMessage("");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("message"));
            verify(connectionFactory, times(4)).createConnection("", "");
            throw e;
        }
    }
}
