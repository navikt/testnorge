package no.nav.identpool.batch.mq.consumer;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

public interface MessageQueue {
    public Connection startConnection() throws JMSException;
    public Session startSession(Connection connection) throws JMSException;
    public String sendMessage(String requestMessageContent) throws JMSException;
    public String sendMessage(
            String requestMessageContent,
            Session session) throws JMSException;
    boolean ping() throws JMSException;
}
