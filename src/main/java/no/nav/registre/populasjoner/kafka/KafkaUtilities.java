package no.nav.registre.populasjoner.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.MDC;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class KafkaUtilities {

    public static final String KAFKA_MDC_KEY = "kafka-key";
    public static final String KAFKA_MDC_TOPIC = "kafka-topic";
    public static final String KAFKA_MDC_PARTITION = "kafka-partition";
    public static final String KAFKA_MDC_OFFSET = "kafka-offset";

    public static void appendToMdc(
            ConsumerRecord<?, ?> record,
            boolean includeKey
    ) {
        MDC.put(KAFKA_MDC_TOPIC, record.topic());
        MDC.put(KAFKA_MDC_PARTITION, String.valueOf(record.partition()));
        MDC.put(KAFKA_MDC_OFFSET, String.valueOf(record.offset()));
        if (includeKey) {
            MDC.put(KAFKA_MDC_KEY, record.key() != null ? record.key().toString() : null);
        }
    }

    public static <K, V> Stream<ConsumerRecord<K, V>> asStream(ConsumerRecords<K, V> records) {
        return StreamSupport.stream(records.spliterator(), false);
    }

}
