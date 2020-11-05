package no.nav.registre.testnorge.identservice.testdata.factories;


import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;

import javax.jms.JMSException;

@FunctionalInterface
public interface MessageFixedQueueServiceFactory {
    MessageQueueConsumer createMessageQueueConsumerWithFixedQueueName(String environment, String fixedQueueName) throws JMSException;
}
