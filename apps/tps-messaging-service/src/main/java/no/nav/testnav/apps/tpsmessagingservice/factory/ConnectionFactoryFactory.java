package no.nav.testnav.apps.tpsmessagingservice.factory;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import no.nav.testnav.apps.tpsmessagingservice.dto.QueueManager;

@FunctionalInterface
public interface ConnectionFactoryFactory {
    ConnectionFactory createConnectionFactory(QueueManager queueManager) throws JMSException;
}
