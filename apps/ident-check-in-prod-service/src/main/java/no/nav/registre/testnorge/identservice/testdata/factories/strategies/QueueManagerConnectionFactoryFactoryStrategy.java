package no.nav.registre.testnorge.identservice.testdata.factories.strategies;

import no.nav.registre.testnorge.identservice.testdata.factories.QueueManager;

/**
 * Provides information needed when creating a queue connection factory
 *
 */
public class QueueManagerConnectionFactoryFactoryStrategy implements ConnectionFactoryFactoryStrategy {

    private QueueManager queueManager;
    private String channelName;

    public QueueManagerConnectionFactoryFactoryStrategy(QueueManager queueManager , String channelName) {
        this.queueManager = queueManager;
        this.channelName = channelName;
    }

    @Override
    public String getName() {
        return queueManager.getName();
    }

    @Override
    public String getHostName() {
        return queueManager.getHostname();
    }

    @Override
    public Integer getPort() {
        return Integer.parseInt( queueManager.getPort() );
    }

    @Override
    public Integer getTransportType() {
        return 1;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }
}
