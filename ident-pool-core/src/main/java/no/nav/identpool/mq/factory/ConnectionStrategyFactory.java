package no.nav.identpool.mq.factory;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.fasit.FasitClient;
import no.nav.identpool.fasit.QueueManager;
import no.nav.identpool.mq.strategy.ConnectionStrategy;

@Component
@RequiredArgsConstructor
public class ConnectionStrategyFactory {

    private static final long CACHE_HOURS_TO_LIVE = 12;
    private static final Cache<String, ConnectionStrategy> cache =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(CACHE_HOURS_TO_LIVE, TimeUnit.HOURS)
                    .build();

    private final FasitClient fasitClient;

    @Value("${mq.channel.postfix}")
    private String channelPostfix;

    ConnectionStrategy createConnectionStrategy(String environment) {
        ConnectionStrategy strategy = cache.getIfPresent(environment);
        if (strategy != null) {
            return strategy;
        }

        String channel = environment + channelPostfix;
        QueueManager queueManager = fasitClient.getQueueManager(environment);
        strategy = new ConnectionStrategy(queueManager.getName(), queueManager.getHostname(), Integer.parseInt(queueManager.getPort()), channel);
        cache.put(environment, strategy);
        return strategy;
    }
}
