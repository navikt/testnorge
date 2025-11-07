package no.nav.testnav.pdllagreservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.Lifecycle;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toMap;

/**
 * PDL sin Custom Kafka Health indicator.
 * <p>
 * Vi har vår egen siden det ikke ser ut som noen av rammeverkene vi bruker tilbyr denne indikatoren.
 * Når Spring-cloud eller andre tilbyr dette out of the box så kan vi fjerne denne.
 */
@Component
@RequiredArgsConstructor
public class KafkaConsumerHealthIndicator implements HealthIndicator {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Setter
    private boolean shutdown = false;

    @Override
    @SneakyThrows
    public Health health() {
        if (shutdown) {
            return Health.up().withDetail("Shutting Down", "true").build();
        }

        val containers = kafkaListenerEndpointRegistry.getAllListenerContainers().stream().toList();
        val running = containers.stream().filter(Lifecycle::isRunning).filter(MessageListenerContainer::isChildRunning).count();
        val total = containers.size();


        val detailsMap = containers.stream().collect(toMap(
                u -> "ListenerId: " + u.getListenerId() + " (GroupId: " + u.getGroupId() + ")",
                u -> (u.isRunning() && u.isChildRunning() ? "RUNNING" : "NOT RUNNING")
                        + " (partitions assigned: " + (u.getAssignedPartitions() == null ? "0" : u.getAssignedPartitions().size()) + ")"
        ));

        if (running == total) {
            return Health.up().withDetails(detailsMap).build();
        } else {
            return Health.outOfService().withDetails(detailsMap).build();
        }
    }
}
