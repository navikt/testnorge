package no.nav.identpool.batch.strategy;

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
}
