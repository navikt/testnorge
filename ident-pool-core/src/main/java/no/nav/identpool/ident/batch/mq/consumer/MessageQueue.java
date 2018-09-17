package no.nav.identpool.ident.batch.mq.consumer;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

public interface MessageQueue {
    public Connection startConnection() throws JMSException;
    public Destination getRequestDestination(Session session) throws JMSException;
    public String sendMessage(String requestMessageContent) throws JMSException;
    public String sendMessage(String requestMessageContent, Session session) throws JMSException;
    boolean ping() throws JMSException;
}
