package no.nav.registre.testnorge.identservice.testdata.factories;

import no.nav.registre.testnorge.identservice.testdata.factories.strategies.ConnectionFactoryFactoryStrategy;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;


@FunctionalInterface
public interface ConnectionFactoryFactory {
    ConnectionFactory createConnectionFactory(ConnectionFactoryFactoryStrategy strategy) throws JMSException;
}
