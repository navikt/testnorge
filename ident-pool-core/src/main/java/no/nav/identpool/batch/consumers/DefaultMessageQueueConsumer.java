package no.nav.identpool.batch.consumers;

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

public class DefaultMessageQueueConsumer implements MessageQueueConsumer {

    private static final long DEFAULT_TIMEOUT = 10000;

    private static final String PING_MESSAGE = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><tpsPersonData xmlns=\"http://www.rtv.no/NamespaceTPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.rtv.no/NamespaceTPS H:\\SYSTEM~1\\SYSTEM~4\\FS03TP~1\\TPSDAT~1.XSD\"><tpsServiceRutine><serviceRutinenavn>FS03-OTILGANG-TILSRTPS-O</serviceRutinenavn></tpsServiceRutine></tpsPersonData>";

    private String username;
    private String password;
    private String requestQueueName;
    private ConnectionFactory connectionFactory;

    public DefaultMessageQueueConsumer(String requestQueueName, ConnectionFactory connectionFactory, String username, String password) {
        this.username = username;
        this.password = password;
        this.requestQueueName = requestQueueName.toUpperCase();
        this.connectionFactory = connectionFactory;
    }

    public String sendMessage(String requestMessageContent) throws JMSException {
        return sendMessageInner(requestMessageContent, DEFAULT_TIMEOUT);
    }

    public String sendMessage(String requestMessageContent, long timeout) throws JMSException {
        return sendMessageInner(requestMessageContent, timeout);
    }

    private String sendMessageInner(String requestMessageContent, long timeout) throws JMSException {

        Connection connection = connectionFactory.createConnection(username, password);
        connection.start();

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
        System.out.println("Message sent");
        TextMessage responseMessage = (TextMessage) consumer.receive(timeout);

        connection.close();

        return responseMessage != null ? responseMessage.getText() : null;
    }

    public void ping() throws JMSException {
        this.sendMessage(PING_MESSAGE);
    }
}
