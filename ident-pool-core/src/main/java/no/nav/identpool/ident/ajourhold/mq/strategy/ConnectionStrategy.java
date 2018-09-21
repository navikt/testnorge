package no.nav.identpool.ident.ajourhold.mq.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConnectionStrategy {

    private final String queueManager;
    private final String hostName;
    private final Integer port;
    private final String channel;
    private final static Integer transportType = 1;

    public Integer getTransportType() {
        return transportType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConnectionStrategy)) {
            return false;
        }
        ConnectionStrategy strategy = (ConnectionStrategy) o;
        return strategy.getQueueManager().equals(this.getQueueManager())
                && strategy.getHostName().equals(this.getHostName())
                && strategy.getPort().equals(this.getPort())
                && strategy.getChannel().equals(this.getChannel());
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + queueManager.hashCode() + channel.hashCode() + port * hostName.hashCode();
        return result;
    }
}
