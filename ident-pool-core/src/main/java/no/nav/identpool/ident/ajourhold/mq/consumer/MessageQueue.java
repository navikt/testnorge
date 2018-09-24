package no.nav.identpool.ident.ajourhold.mq.consumer;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.jms.JMSException;

public interface MessageQueue {
    public String sendMessage(String requestMessageContent) throws JMSException;
    public void sendMessages(BiConsumer<Integer, String> consumer, Function<Integer, Boolean> ignoreIndex, String... requestMessages) throws JMSException;
    public void sendMessages(BiConsumer<Integer, String> consumer, Function<Integer, Boolean> ignoreIndex, int startIndex, int stopIndex, String... requestMessages) throws JMSException;
    boolean ping() throws JMSException;
}
