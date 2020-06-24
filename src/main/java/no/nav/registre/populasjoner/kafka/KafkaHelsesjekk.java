package no.nav.registre.populasjoner.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ContainerAwareErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;

@Slf4j
@Component
public class KafkaHelsesjekk implements HealthCheck, ContainerAwareErrorHandler {

    private static final long ETT_MINUTT = 60_000L;

    private volatile long lastThrownExceptionTime;

    private volatile Exception lastThrownException;

    @Override
    public HealthCheckResult checkHealth() {
        boolean hasFailedRecently = (lastThrownExceptionTime + ETT_MINUTT) > System.currentTimeMillis();

        if (hasFailedRecently) {
            return HealthCheckResult.unhealthy("Kafka consumer feilet " + new Date(lastThrownExceptionTime), lastThrownException);
        }

        return HealthCheckResult.healthy();
    }

    @Override
    public void handle(
            Exception thrownException,
            List<ConsumerRecord<?, ?>> records,
            Consumer<?, ?> consumer,
            MessageListenerContainer container
    ) {
        log.error("Feil i listener", thrownException);
        lastThrownExceptionTime = System.currentTimeMillis();
        lastThrownException = thrownException;
    }
}
