package no.nav.testnav.pdldatalagreservice.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * PDL sin Custom Kafka Health indicator.
 * <p>
 * Vi har vår egen siden det ikke ser ut som noen av rammeverkene vi bruker tilbyr denne indikatoren.
 * Når Spring-cloud eller andre tilbyr dette out of the box så kan vi fjerne denne.
 */
@Component
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    private final AdminClient kafkaAdminClient;

    @Override
    @SneakyThrows
    public Health health() {
        // If Kafka is not connected, describeCluster() method throws
        // an exception which in turn sets this indicator as being DOWN.
        try {
            val options = new DescribeClusterOptions().timeoutMs(1000);

            val clusterDescription = kafkaAdminClient.describeCluster(options);
            return Health.up()
                    .withDetail("clusterId", clusterDescription.clusterId().get())
                    .withDetail("nodeCount", clusterDescription.nodes().get().size())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("Exception", e.getMessage())
                    .build();
        }
    }
}
