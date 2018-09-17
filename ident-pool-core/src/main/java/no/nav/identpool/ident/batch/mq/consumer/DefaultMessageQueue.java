package no.nav.identpool.ident.batch.mq.consumer;

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

import sun.security.krb5.internal.crypto.Des;

public class DefaultMessageQueue implements MessageQueue {

    private static final long DEFAULT_TIMEOUT = 5000;

    private static final String PING_MESSAGE = "<?service version=\"1.0\" encoding=\"ISO-8859-1\"?><tpsPersonData xmlns=\"http://www.rtv.no/NamespaceTPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.rtv.no/NamespaceTPS H:\\SYSTEM~1\\SYSTEM~4\\FS03TP~1\\TPSDAT~1.XSD\"><tpsServiceRutine><serviceRutinenavn>FS03-OTILGANG-TILSRTPS-O</serviceRutinenavn></tpsServiceRutine></tpsPersonData>";

    private String username;
    private String password;
    private String requestQueueName;
    private ConnectionFactory connectionFactory;

    public DefaultMessageQueue(String requestQueueName, ConnectionFactory connectionFactory, String username, String password) {
        this.username = username;
        this.password = password;
        this.requestQueueName = requestQueueName.toUpperCase();
        this.connectionFactory = connectionFactory;
    }

    public Connection startConnection() throws JMSException {
        Connection connection = connectionFactory.createConnection(username, password);
        connection.start();
        return connection;
    }

    public Destination getRequestDestination(Session session) throws JMSException {
        return session.createQueue(requestQueueName);
    }

    public String sendMessage(String requestMessageContent) throws JMSException {
        Connection connection = connectionFactory.createConnection(username, password);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        String response = sendMessage(requestMessageContent, session);
        session.close();
        connection.close();
        return response;
    }

    public String sendMessage(String requestMessageContent, Session session) throws JMSException {

        Destination requestDestination = session.createQueue(requestQueueName);
        Destination responseDestination = session.createTemporaryQueue();

        if (requestDestination instanceof MQQueue) {
            ((MQQueue) requestDestination).setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);         //TODO: This method should be provider independent?
        }

        /* Prepare request message */
        TextMessage requestMessage = session.createTextMessage(requestMessageContent);
        MessageProducer producer = session.createProducer(requestDestination);

        requestMessage.setJMSReplyTo(responseDestination);
        producer.send(requestMessage);

        String attributes = String.format("JMSCorrelationID='%s'", requestMessage.getJMSMessageID());
        MessageConsumer consumer = session.createConsumer(responseDestination, attributes);
        TextMessage responseMessage = (TextMessage) consumer.receive(DEFAULT_TIMEOUT);

        return responseMessage != null ? responseMessage.getText() : "";
    }

    public boolean ping() throws JMSException {
        this.sendMessage(PING_MESSAGE);
        return true;
    }
}
