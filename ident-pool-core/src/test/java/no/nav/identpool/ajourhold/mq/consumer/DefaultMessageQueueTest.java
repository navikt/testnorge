package no.nav.identpool.ajourhold.mq.consumer;

import no.nav.identpool.test.mockito.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

@ExtendWith(MockitoExtension.class)
class DefaultMessageQueueTest {

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

    @BeforeEach
    void init() throws JMSException {
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

    //TODO Legge til @DisplayName rundt omkring
    //TODO Skrive bedre test
    @Test
    @DisplayName("Ping skal ikke kaste exception")
    void ping() throws JMSException {
        messageQueue.ping();
    }

    @Test
    void send() throws JMSException {
        assertThat(messageQueue.sendMessage(""), is("PING!"));
    }

    @Test
    void connectionError() throws JMSException {
        when(connectionFactory.createConnection("", "")).thenThrow(new JMSException("message"));
        JMSException thrown = assertThrows(JMSException.class, () -> messageQueue.sendMessage(""));
        assertThat(thrown.getMessage(), is("message"));
//        verify(connectionFactory, times(4)).createConnection("", "");
    }

    @Test
    void sessionError() throws JMSException {
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenThrow(new JMSException("message"));
        JMSException thrown = assertThrows(JMSException.class, () -> messageQueue.sendMessage(""));
        assertThat(thrown.getMessage(), is("message"));
//        verify(connectionFactory, times(4)).createConnection("", "");
    }
}
