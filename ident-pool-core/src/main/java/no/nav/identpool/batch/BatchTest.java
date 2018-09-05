package no.nav.identpool.batch;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.batch.consumers.MessageQueueConsumer;
import no.nav.identpool.batch.factory.MessageQueueFactory;

@Component
@RequiredArgsConstructor
public class BatchTest {
    private final MessageQueueFactory messageQueueFactory;

    @PostConstruct
    private void init() throws JMSException {
        MessageQueueConsumer messageQueueConsumer = messageQueueFactory.createMessageQueue("t9");
        messageQueueConsumer.ping();
    }
}
