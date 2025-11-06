package no.nav.testnav.pdlpersonopensearchservice.utility;

import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class KafkaUtilities {
    public static final String KAFKA_MDC_KEY = "kafka-key";
    public static final String KAFKA_MDC_TOPIC = "kafka-topic";
    public static final String KAFKA_MDC_PARTITION = "kafka-partition";
    public static final String KAFKA_MDC_OFFSET = "kafka-offset";

    public static void appendToMdc(ConsumerRecord<?, ?> record, boolean includeKey) {
        MDC.put(KAFKA_MDC_TOPIC, record.topic());
        MDC.put(KAFKA_MDC_PARTITION, String.valueOf(record.partition()));
        MDC.put(KAFKA_MDC_OFFSET, String.valueOf(record.offset()));
        if (includeKey) {
            MDC.put(KAFKA_MDC_KEY, record.key() != null ? record.key().toString() : null);
        }
    }

    public static void assignRandomTransactionIdPrefix() {
        System.setProperty("kafka-transaction-uuid", UUID.randomUUID().toString().substring(0, 8));
    }

    public static String getLastHeaderByKeyAsString(Headers headers, String key, String defaultValue) {
        val header = headers.lastHeader(key);
        return header != null && header.value() != null ? new String(header.value(), StandardCharsets.UTF_8) : defaultValue;
    }

    public static List<String> getAllHeadersByKeyAsStrings(Headers headers, String key) {
        return StreamSupport.stream(headers.spliterator(), false)
                .filter(e -> e.key().equals(key))
                .map(Header::value)
                .filter(Objects::nonNull)
                .map(e -> new String(e, StandardCharsets.UTF_8))
                .toList();
    }

    public static List<CharSequence> stringListToCharSequenceList(List<String> entries) {
        return entries.stream().map(e -> (CharSequence) e).toList();
    }

    public static boolean isTombstone(ConsumerRecord<?, ?> record) {
        return record.value() == null;
    }

    public static boolean isNotTombstone(ConsumerRecord<?, ?> record) {
        return record.value() != null;
    }

    public static <K, V> Stream<ConsumerRecord<K, V>> asStream(ConsumerRecords<K, V> records) {
        return StreamSupport.stream(records.spliterator(), false);
    }

    public static Integer partition(Object key, int partitions) {
        return Utils.toPositive(key.hashCode()) % partitions;
    }
}