package no.nav.testnav.apps.tpsmessagingservice.factory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;

@FunctionalInterface
public interface ConnectionFactoryFactory {
    ConnectionFactory createConnectionFactory(QueueManager queueManager) throws JMSException;
}
