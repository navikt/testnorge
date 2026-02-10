package no.nav.testnav.pdllagreservice.utility;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.MDC;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@UtilityClass
public class KafkaUtilities {
    public static final String KAFKA_MDC_KEY = "kafka-key";
    public static final String KAFKA_MDC_TOPIC = "kafka-topic";
    public static final String KAFKA_MDC_PARTITION = "kafka-partition";
    public static final String KAFKA_MDC_OFFSET = "kafka-offset";

    public static void appendToMdc(ConsumerRecord<?, ?> record1, boolean includeKey) {

        MDC.put(KAFKA_MDC_TOPIC, record1.topic());
        MDC.put(KAFKA_MDC_PARTITION, String.valueOf(record1.partition()));
        MDC.put(KAFKA_MDC_OFFSET, String.valueOf(record1.offset()));
        if (includeKey) {
            MDC.put(KAFKA_MDC_KEY, record1.key() != null ? record1.key().toString() : null);
        }
    }

    public static <K, V> Stream<ConsumerRecord<K, V>> asStream(ConsumerRecords<K, V> records) {
        return StreamSupport.stream(records.spliterator(), false);
    }
}