package no.nav.identpool.ident.ajourhold.mq.consumer;

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

    public String sendMessage(String requestMessage) throws JMSException {
        return sendMessageConnection(requestMessage, RETRYCOUNT);
    }

    private String sendMessageConnection(String requestMessage, int retryCount) throws JMSException {
        try (Connection connection = connectionFactory.createConnection(username, password)) {
            connection.start();
            return sendMessageSession(requestMessage, connection, RETRYCOUNT);
        } catch (JMSException e) {
            if (retryCount > 0) {
                return this.sendMessageConnection(requestMessage, retryCount - 1);
            } else {
                throw e;
            }
        }
    }

    private String sendMessageSession(String requestMessage, Connection connection, int retryCount) throws JMSException {

        try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            return sendMessageQueue(requestMessage, session);
        } catch (JMSException e) {
            if (retryCount > 0) {
                return this.sendMessageSession(requestMessage, connection, retryCount - 1);
            } else {
                throw e;
            }
        }
    }

    private String sendMessageQueue(String requestMessageContent, Session session) throws JMSException {
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