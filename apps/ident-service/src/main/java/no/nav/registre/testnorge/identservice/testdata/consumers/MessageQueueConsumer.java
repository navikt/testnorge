package no.nav.registre.testnorge.identservice.testdata.consumers;

import com.ibm.mq.jms.MQQueue;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

@Slf4j
public class MessageQueueConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueueConsumer.class);
    private static final String FEIL_KOENAVN = "Feil i koenavn eller miljoe";

    private final String requestQueueName;
    private final ConnectionFactory connectionFactory;

    private final String password;
    private final String username;

    public MessageQueueConsumer(
            String requestQueueName,
            ConnectionFactory connectionFactory,
            String username,
            String password
    ) {
        this.requestQueueName = requestQueueName;
        this.connectionFactory = connectionFactory;
        this.username = username;
        this.password = password;
    }

    public String sendMessage(String requestMessageContent, long timeout) throws JMSException {

        TextMessage responseMessage;
        try (Connection connection = connectionFactory.createConnection(username, password)) {
            connection.start();

            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

                /* Prepare destinations */
                Destination requestDestination = session.createQueue(requestQueueName);

                Destination responseDestination;
                if (requestQueueName.toUpperCase().contains("SFE")) {
                    responseDestination = session.createQueue(requestQueueName.toUpperCase() + "_REPLY");
                } else {
                    responseDestination = createTemporaryQueueFor(session);
                }

                if (requestDestination instanceof MQQueue) {
                    ((MQQueue) requestDestination).setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);         //TODO: This method should be provider independent
                }

                /* Prepare request message */
                TextMessage requestMessage = session.createTextMessage(requestMessageContent);
                try {
                    try (MessageProducer producer = session.createProducer(requestDestination)) {
                        requestMessage.setJMSReplyTo(responseDestination);

                        producer.send(requestMessage);
                    }
                } catch (JMSException e) {
                    LOGGER.warn(String.format("%s: %s", FEIL_KOENAVN, e.getMessage()), e);
                    return e.getMessage();
                }

                responseMessage = null;
                if (timeout > 0) {
                    /* Wait for response */
                    String attributes = String.format("JMSCorrelationID='%s'", requestMessage.getJMSMessageID());

                    try (MessageConsumer consumer = session.createConsumer(responseDestination, attributes)) {

                        responseMessage = (TextMessage) consumer.receive(timeout);
                    }
                }
            }
        }

        return responseMessage != null ? responseMessage.getText() : "";
    }

    public Destination createTemporaryQueueFor(Session session) throws JMSException {
        return session.createTemporaryQueue();
    }

}
