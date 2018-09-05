package no.nav.identpool.batch.consumers;

import javax.jms.JMSException;

public interface MessageQueueConsumer {
    String sendMessage(String requestMessageContent) throws JMSException;
    String sendMessage(String requestMessageContent, long timeout) throws JMSException;
    void ping() throws JMSException;
}
