package no.nav.identpool.ident.ajourhold.mq.consumer;

import javax.jms.JMSException;

public interface MessageQueue {

    String sendMessage(String requestMessageContent) throws JMSException;

    boolean ping() throws JMSException;
}
