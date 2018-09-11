package no.nav.identpool.batch.domain.mq.consumer;

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

    public String sendMessage(String requestMessageContent) throws JMSException {
        Connection connection = connectionFactory.createConnection(username, password);
        connection.start();
        String response = sendMessage(requestMessageContent, connection);
        connection.close();
        return response;
    }

    public String sendMessage(String requestMessageContent, Connection connection) throws JMSException {

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

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

        session.close();

        return responseMessage != null ? responseMessage.getText() : null;
    }

    public boolean ping() throws JMSException {
        this.sendMessage(PING_MESSAGE);
        return true;
    }
}
