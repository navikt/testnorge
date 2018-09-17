package no.nav.identpool.ident.batch.mq.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import no.nav.freg.fasit.utils.domain.QueueManager;
import no.nav.identpool.ident.batch.fasit.FasitClient;
import no.nav.identpool.ident.batch.mq.strategy.ConnectionStrategy;

@Component
@RequiredArgsConstructor
public class ConnectionStrategyFactory {

    @Value("${mq.channel.postfix}")
    private String CHANNEL_POSTFIX;

    private final FasitClient fasitClient;

    ConnectionStrategy createConnectionStrategy(String environment) {
        String channel = environment + CHANNEL_POSTFIX;
        QueueManager queueManager = fasitClient.getQueueManager(environment);
        return new ConnectionStrategy(queueManager.getName(), queueManager.getHostname(), Integer.parseInt(queueManager.getPort()), channel);
    }
}
