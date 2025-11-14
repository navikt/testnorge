package no.nav.testnav.pdllagreservice.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.context.Lifecycle;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.event.NonResponsiveConsumerEvent;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class RestartingErrorHandler implements CommonErrorHandler {
    private static final CommonContainerStoppingErrorHandler STOPPER = new CommonContainerStoppingErrorHandler();
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final MeterRegistry meterRegistry;

    @Getter
    private final AtomicBoolean enabled = new AtomicBoolean(true);

    @EventListener
    public void eventHandler(NonResponsiveConsumerEvent event) {
        long timeSinceLastPoll = Duration.of(event.getTimeSinceLastPoll(), ChronoUnit.MILLIS).toMinutes();
        log.error("scheduledRestartChecker: kafka problem detected (last poll was {} minutes ago), expect restart in 1-2 min", timeSinceLastPoll);
        log.warn("NonResponsiveConsumerEvent:{}", event);
        Thread.ofVirtual().start(this::restart);

    }

    private void restart() {
        kafkaListenerEndpointRegistry.stop(this::scheduledRestartChecker);
    }

    @Override
    public void handleRemaining(Exception thrownException, List<ConsumerRecord<?, ?>> data, Consumer<?, ?> consumer, MessageListenerContainer container) {
        logAndHandleException(thrownException, consumer, container);
    }

    @Override
    public void handleBatch(Exception thrownException, ConsumerRecords<?, ?> data, Consumer<?, ?> consumer, MessageListenerContainer container, Runnable invokeListener) {
        logAndHandleException(thrownException, consumer, container);
    }

    @Override
    public boolean handleOne(Exception thrownException, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer, MessageListenerContainer container) {
        logAndHandleException(thrownException, consumer, container);
        return false;
    }

    private static void logAndHandleException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container) {
        log.warn("scheduledRestartChecker: kafka problem detected, expect restart in 1-2 min {}", thrownException.getMessage());
        STOPPER.handleOtherException(thrownException, consumer, container, false);
    }


    @WithSpan
    @Scheduled(initialDelay = 180_000, fixedRate = 60_000)
    private void scheduledRestartChecker() {
        try {
            log.debug("Checking MessageListenerContainer(s) status");
            if (enabled.get()) {
                if (!kafkaListenerEndpointRegistry.isRunning()) {
                    log.warn("kafkaListenerEndpointRegistry not running: attempting to start kafkaListenerEndpointRegistry");
                    kafkaListenerEndpointRegistry.start();
                }

                val containers = kafkaListenerEndpointRegistry.getAllListenerContainers();
                val running = containers.stream().filter(Lifecycle::isRunning).count();
                val total = containers.size();

                meterRegistry.gauge("kafka.listeners", Tags.of("error.handler", "running"), running);
                meterRegistry.gauge("kafka.listeners", Tags.of("error.handler", "total"), total);

                if (running < total) {
                    log.warn("RestartingErrorHandler: only {} of {} kafka MessageListenerContainer running; attempting to restart the others", running, total);
                    kafkaListenerEndpointRegistry.start(); //( kjører startIfNecessary på alle listeners)
                }
            }
        } catch (Exception e) {
            log.error("RestartingErrorHandler Exception", e);
        }
    }
}