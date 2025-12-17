package no.nav.testnav.pdllagreservice.kafka;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import java.time.Duration;

@UtilityClass
public class KafkaContainerUtils {
    public static final String DEFAULT_SPRING_KAFKA_FACTORY_NAME = "kafkaListenerContainerFactory";

    public static <K, V> ConcurrentKafkaListenerContainerFactory<K, V> containerFactory(
            ConsumerFactory<K, V> kafkaConsumerFactory,
            long msWait) {

        val factory = new ConcurrentKafkaListenerContainerFactory<K, V>();
        factory.setConsumerFactory(kafkaConsumerFactory);
        factory.getContainerProperties().setAuthExceptionRetryInterval(Duration.ofMillis(msWait));
        factory.getContainerProperties().setSubBatchPerPartition(false);
        return factory;
    }
}
