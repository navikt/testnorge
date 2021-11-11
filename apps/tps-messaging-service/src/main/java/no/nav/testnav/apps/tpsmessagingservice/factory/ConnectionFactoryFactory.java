package no.nav.testnav.apps.tpsmessagingservice.factory;

import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@FunctionalInterface
public interface ConnectionFactoryFactory {
    ConnectionFactory createConnectionFactory(QueueManager queueManager) throws JMSException;
}
