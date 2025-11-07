package no.nav.testnav.pdllagreservice.kafka;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import java.time.Duration;

@UtilityClass
public class KafkaContainerUtils {
    public static final String DEFAULT_SPRING_KAFKA_FACTORY_NAME = "kafkaListenerContainerFactory";

    public static ConcurrentKafkaListenerContainerFactory<?, ?> containerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory,
            long msWait) {

        val factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        // Må være mindre enn max.poll.interval.ms. Denne er default 30 sekunder.
        factory.getContainerProperties().setAuthExceptionRetryInterval(Duration.ofMillis(msWait));
        factory.getContainerProperties().setSubBatchPerPartition(false);
        return factory;
    }
}
