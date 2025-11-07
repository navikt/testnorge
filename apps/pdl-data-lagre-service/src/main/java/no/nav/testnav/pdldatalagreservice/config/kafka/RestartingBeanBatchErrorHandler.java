package no.nav.testnav.pdldatalagreservice.config.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.function.Predicate.not;

/**
 * Kafka listeners som lages "manuelt" vil ikke bli registrert i kafkaListenerEndpointRegistry, men vil  være beans istedet
 * Disse har derfor en litt annen måte å  ble restartet på en den vi har brukt i de andre restarterene.
 */
@Slf4j
@RequiredArgsConstructor
public class RestartingBeanBatchErrorHandler {

    private final List<AbstractMessageListenerContainer<String, String>> messageListenerContainers;
    private final MeterRegistry meterRegistry;

    @Getter
    private AtomicBoolean enabled = new AtomicBoolean(true);


    @WithSpan
    @Scheduled(initialDelay = 180_000, fixedRate = 60_000)
    private void scheduledRestartChecker() {
        try {
            log.debug("Checking MessageListenerContainer(s) status");
            if (enabled.get()) {
                val total = messageListenerContainers.size();
                val running = messageListenerContainers.stream().filter(AbstractMessageListenerContainer::isRunning).count();


                meterRegistry.gauge("kafka.listeners", Tags.of("batch.error.handler", "running"), running);
                meterRegistry.gauge("kafka.listeners", Tags.of("batch.error.handler", "total"), total);

                if (running < total) {
                    log.warn("RestartingBeanBatchErrorHandler: only {} of {} kafka MessageListenerContainer running; attempting to restart the others", running, total);
                    messageListenerContainers.stream().filter(not(AbstractMessageListenerContainer::isRunning)).forEach(this::restartContainer);
                }
            }
        } catch (Exception e) {
            log.error("RestartingErrorHandler Exception", e);
        }
    }

    private void restartContainer(AbstractMessageListenerContainer<String, String> stringStringAbstractMessageListenerContainer) {
        try {
            stringStringAbstractMessageListenerContainer.start();
        } catch (Exception e) {
            log.error("Unable to restart KafkaListener", e);
        }
    }
}