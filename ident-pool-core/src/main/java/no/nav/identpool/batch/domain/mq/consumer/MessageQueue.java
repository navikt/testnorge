package no.nav.identpool.batch.domain.mq.consumer;

import javax.jms.Connection;
import javax.jms.JMSException;

public interface MessageQueue {
    public Connection startConnection() throws JMSException;
    public String sendMessage(String requestMessageContent) throws JMSException;
    public String sendMessage(
            String requestMessageContent,
            Connection connection) throws JMSException;
    boolean ping() throws JMSException;
}
