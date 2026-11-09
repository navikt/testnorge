package no.nav.dolly.web.config.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.session.MapSession;
import org.springframework.session.data.redis.RedisSessionMapper;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
public class UnreadableSessionDiscardingMapper implements BiFunction<String, Map<String, Object>, Mono<MapSession>> {

    private final RedisSessionMapper delegate = new RedisSessionMapper();

    @Override
    public Mono<MapSession> apply(String sessionId, Map<String, Object> map) {
        var reasons = map
                .values()
                .stream()
                .filter(UnreadableSessionValue.class::isInstance)
                .map(UnreadableSessionValue.class::cast)
                .collect(Collectors.toCollection(TreeSet::new));
        if (!reasons.isEmpty()) {
            log.warn("Forkaster sesjon som ikke kan leses, årsak: {}", reasons);
            return Mono.empty();
        }
        return Mono.fromSupplier(() -> delegate.apply(sessionId, map));
    }
}
