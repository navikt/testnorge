package no.nav.identpool.ident.batch.mq.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConnectionStrategy {

    private final String queueManager;
    private final String hostName;
    private final Integer port;
    private final String channel;

    public Integer getTransportType() {
        return 1;
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
        result = 31 * result + (queueManager.hashCode() + hostName.hashCode() + port) / channel.hashCode();
        return result;
    }
}
