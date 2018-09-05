package no.nav.identpool.batch.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.identpool.batch.strategy.ConnectionStrategy;

@Component
public class ConnectionStrategyFactory {

    @Value("${MQGATEWAY_NAME}")
    private String queueManagerAlias;

    @Value("${MQGATEWAY_HOSTNAME}")
    private String hostname;

    @Value("${MQGATEWAY_PORT}")
    private Integer port;

    @Value("${messagequeue.channel.postfix}")
    private String CHANNEL_POSTFIX;

    ConnectionStrategy createConnectionStrategy(String environment) {
        String channel = environment.toUpperCase() + CHANNEL_POSTFIX;
        return new ConnectionStrategy(queueManagerAlias, hostname, port, channel);
    }
}
