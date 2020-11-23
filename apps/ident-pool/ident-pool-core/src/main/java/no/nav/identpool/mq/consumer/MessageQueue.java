package no.nav.identpool.mq.consumer;

import javax.jms.JMSException;

public interface MessageQueue {

    String sendMessage(String requestMessageContent) throws JMSException;

    void ping() throws JMSException;
}
