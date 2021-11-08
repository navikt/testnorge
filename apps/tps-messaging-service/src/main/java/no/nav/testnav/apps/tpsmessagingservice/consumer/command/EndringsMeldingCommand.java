package no.nav.testnav.apps.tpsmessagingservice.consumer.command;

import java.util.concurrent.Callable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import com.ibm.mq.jms.MQQueue;
import com.ibm.msg.client.wmq.compat.jms.internal.JMSC;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EndringsMeldingCommand implements Callable<String> {

    private static final long TIMEOUT_VAL = 5000;
    private static final String FEIL_KOENAVN = "Feil i kønavn eller miljø";

    private final ConnectionFactory connectionFactory;
    private final String requestQueueName;
    private final String username;
    private final String password;
    private final String requestMessageContent;

    public String call() throws JMSException {

        try (Connection connection = connectionFactory.createConnection(username, password)) {
            connection.start();

            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

                /* Prepare destinations */
                var requestDestination = session.createQueue(requestQueueName);

                Destination responseDestination;
                if (requestQueueName.toUpperCase().contains("SFE")) {
                    responseDestination = session.createQueue(requestQueueName.toUpperCase() + "_REPLY");
                } else {
                    responseDestination = session.createTemporaryQueue();
                }

                if (requestDestination instanceof MQQueue) {
                    ((MQQueue) requestDestination).setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);
                }

                /* Prepare request message */
                var requestMessage = session.createTextMessage(requestMessageContent);
                try {
                    try (MessageProducer producer = session.createProducer(requestDestination)) {
                        requestMessage.setJMSReplyTo(responseDestination);

                        producer.send(requestMessage);
                    }
                } catch (JMSException e) {
                    log.warn(String.format("%s: %s", FEIL_KOENAVN, e.getMessage()), e);
                    return e.getMessage();
                }

                TextMessage responseMessage;

                /* Wait for response */
                String attributes = String.format("JMSCorrelationID='%s'", requestMessage.getJMSMessageID());

                try (MessageConsumer consumer = session.createConsumer(responseDestination, attributes)) {

                    responseMessage = (TextMessage) consumer.receive(TIMEOUT_VAL);
                }

                return responseMessage != null ? responseMessage.getText() : "";
            }
        }
    }
}
