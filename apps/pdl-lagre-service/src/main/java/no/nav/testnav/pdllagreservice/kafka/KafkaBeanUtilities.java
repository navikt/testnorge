package no.nav.testnav.pdllagreservice.kafka;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class KafkaBeanUtilities {

    public DefaultKafkaConsumerFactory<Object, Object> stringStringConsumerFactory(Map<String, Object> ConfigurationProperties) {
        return customConsumerFactory(ConfigurationProperties, StringDeserializer.class, StringDeserializer.class);
    }

    public DefaultKafkaConsumerFactory<Object, Object> longStringConsumerFactory(Map<String, Object> configurationProperties) {
        return customConsumerFactory(configurationProperties, LongDeserializer.class, StringDeserializer.class);
    }

    public DefaultKafkaConsumerFactory<Object, Object> customConsumerFactory(Map<String, Object> oldConfigurationProperties,
                                                                             Class keyDeserializerClass,
                                                                             Class valueDeserializerClass) {
        val newConfigurationProperties = new HashMap<>(oldConfigurationProperties);
        newConfigurationProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass.getName());
        newConfigurationProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass.getName());
        return new DefaultKafkaConsumerFactory<>(newConfigurationProperties);
    }
}
