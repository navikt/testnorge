package no.nav.testnav.apps.tpsmessagingservice.consumer.command;

import com.ibm.mq.jakarta.jms.MQQueue;
import com.ibm.msg.client.jakarta.wmq.compat.jms.internal.JMSC;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@RequiredArgsConstructor
public class TpsMeldingCommand implements Callable<String> {

    public static final String NO_RESPONSE = "zzzZZZzzz";
    private static final long TIMEOUT_VAL = 10000;
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

                if (requestDestination instanceof MQQueue destination) {
                    destination.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);
                }

                /* Prepare request message */
                var requestMessage = session.createTextMessage(requestMessageContent);
                try {
                    try (MessageProducer producer = session.createProducer(requestDestination)) {
                        requestMessage.setJMSReplyTo(responseDestination);

                        producer.send(requestMessage);
                    }
                } catch (JMSException e) {
                    log.warn("%s: %s".formatted(FEIL_KOENAVN, e.getMessage()), e);
                    return e.getMessage();
                }

                TextMessage responseMessage;

                /* Wait for response */
                String attributes = "JMSCorrelationID='%s'".formatted(requestMessage.getJMSMessageID());

                try (MessageConsumer consumer = session.createConsumer(responseDestination, attributes)) {

                    responseMessage = (TextMessage) consumer.receive(TIMEOUT_VAL);
                }

                return nonNull(responseMessage) && isNotBlank(requestMessage.getText()) ? responseMessage.getText() : NO_RESPONSE;
            }
        }
    }
}
