package no.nav.identpool.ident.ajourhold.mq.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import com.ibm.mq.jms.MQQueue;
import com.ibm.msg.client.wmq.v6.jms.internal.JMSC;

public class DefaultMessageQueue implements MessageQueue {

    private static final int RETRYCOUNT = 3;
    private static final long DEFAULT_TIMEOUT = 5000;
    private static final String PING_MESSAGE = "<?service version=\"1.0\" encoding=\"ISO-8859-1\"?><tpsPersonData xmlns=\"http://www.rtv.no/NamespaceTPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.rtv.no/NamespaceTPS H:\\SYSTEM~1\\SYSTEM~4\\FS03TP~1\\TPSDAT~1.XSD\"><tpsServiceRutine><serviceRutinenavn>FS03-OTILGANG-TILSRTPS-O</serviceRutinenavn></tpsServiceRutine></tpsPersonData>";

    private final String username;
    private final String password;
    private final String requestQueueName;
    private final ConnectionFactory connectionFactory;

    public DefaultMessageQueue(String requestQueueName, ConnectionFactory connectionFactory, String username, String password) {
        this.username = username;
        this.password = password;
        this.requestQueueName = requestQueueName.toUpperCase();
        this.connectionFactory = connectionFactory;
    }

    public String sendMessage(String requestMessageContent) throws JMSException {
        String[] array = new String[1];
        sendMessages((ignored, s) -> array[0] = s, i -> false, requestMessageContent);
        return array[0];
    }

    public void sendMessages(
            BiConsumer<Integer, String> consumer,
            Function<Integer, Boolean> ignoreIndex,
            String... requestMessages) throws JMSException {
        List<String> messages = new ArrayList<>(requestMessages.length);
        Collections.addAll(messages, requestMessages);
        sendMessageConnection(consumer, ignoreIndex, messages, 0, RETRYCOUNT);
    }

    public void sendMessages(
            BiConsumer<Integer, String> consumer,
            Function<Integer, Boolean> ignoreIndex,
            int startIndex,
            int stopIndex,
            String... requestMessages) throws JMSException {
        List<String> messages = Arrays.stream(requestMessages, startIndex, stopIndex)
                .collect(Collectors.toList());
        sendMessageConnection(consumer, ignoreIndex, messages, 0, RETRYCOUNT);
    }

    private void sendMessageConnection(
            BiConsumer<Integer, String> consumer,
            Function<Integer, Boolean> ignoreIndex,
            List<String> requestMessages,
            int index, int retryCount) throws JMSException {
        try (Connection connection = connectionFactory.createConnection(username, password)) {
            connection.start();
            sendMessageSession(consumer, ignoreIndex, requestMessages, connection, index, RETRYCOUNT);
        } catch (JMSException e) {
            if (retryCount > 0) {
                this.sendMessageConnection(consumer, ignoreIndex, requestMessages, index, retryCount - 1);
            } else {
                throw e;
            }
        }
    }

    private void sendMessageSession(
            BiConsumer<Integer, String> consumer,
            Function<Integer, Boolean> ignoreIndex,
            List<String> requestMessages,
            Connection connection,
            int index, int retryCount) throws JMSException {

        try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            Iterator<String> iterator = requestMessages.iterator();
            while (iterator.hasNext()) {
                String message = iterator.next();
                if (!ignoreIndex.apply(index)) {
                    consumer.accept(index, sendMessage(message, session));
                }
                iterator.remove();
                index += 1;
                retryCount = RETRYCOUNT;
            }
        } catch (JMSException e) {
            if (retryCount > 0) {
                this.sendMessageSession(consumer, ignoreIndex, requestMessages, connection, index, retryCount - 1);
            } else {
                throw e;
            }
        }
    }

    private String sendMessage(String requestMessageContent, Session session) throws JMSException {
        Destination requestDestination = session.createQueue(requestQueueName);
        Destination responseDestination = session.createTemporaryQueue();

        if (requestDestination instanceof MQQueue) {
            ((MQQueue) requestDestination).setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);
        }
        String responseMessage;
        /* Prepare request message */
        TextMessage requestMessage = session.createTextMessage(requestMessageContent);
        try (MessageProducer producer = session.createProducer(requestDestination)) {

            requestMessage.setJMSReplyTo(responseDestination);
            producer.send(requestMessage);

            String attributes = String.format("JMSCorrelationID='%s'", requestMessage.getJMSMessageID());
            responseMessage = consumerReceive(session, responseDestination, attributes);
        }
        return responseMessage;
    }

    private String consumerReceive(Session session, Destination responseDestination, String attributes) throws JMSException {
        TextMessage responseMessage;
        try (MessageConsumer consumer = session.createConsumer(responseDestination, attributes)) {
            responseMessage = (TextMessage) consumer.receive(DEFAULT_TIMEOUT);
        }
        return responseMessage != null ? responseMessage.getText() : "";
    }

    public boolean ping() throws JMSException {
        return !"".equals(this.sendMessage(PING_MESSAGE));
    }
}