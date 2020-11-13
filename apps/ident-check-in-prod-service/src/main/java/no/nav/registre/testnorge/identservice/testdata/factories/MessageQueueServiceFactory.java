package no.nav.registre.testnorge.identservice.testdata.factories;

import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;

import javax.jms.JMSException;


@FunctionalInterface
public interface MessageQueueServiceFactory {
    MessageQueueConsumer createMessageQueueConsumer(String environment, String requestQueueAlias, boolean isQueueName) throws JMSException;
}
