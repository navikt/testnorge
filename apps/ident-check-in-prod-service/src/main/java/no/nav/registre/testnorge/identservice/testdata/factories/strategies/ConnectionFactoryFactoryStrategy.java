package no.nav.registre.testnorge.identservice.testdata.factories.strategies;

/**
 * Provides information needed when creating a queue connection factory
 *
 */
public interface ConnectionFactoryFactoryStrategy {
    String getName();
    String getHostName();
    String getChannelName();
    Integer getPort();
    Integer getTransportType();
}
