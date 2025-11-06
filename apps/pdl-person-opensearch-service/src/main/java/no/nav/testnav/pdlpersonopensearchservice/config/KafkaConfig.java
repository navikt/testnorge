package no.nav.testnav.pdlpersonopensearchservice.config;


import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;

import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

@Slf4j
@EnableKafka
@Configuration
@Profile({"dev", "prod"})
public class KafkaConfig {

    private static final Random RANDOM = new SecureRandom();
    private final String groupId;

    public KafkaConfig(@Value("${spring.kafka.consumer.group-id}") String groupId) {
        this.groupId = groupId;
    }

    public ConsumerFactory<String, String> consumerFactory() {

        var randomSuffixGroupID = String.valueOf((int)(RANDOM.nextFloat() * 1000));

        var inetSocketAddress = new InetSocketAddress(0);
        var props = new HashMap<String, Object>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BROKERS"));
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, System.getenv("KAFKA_KEYSTORE_PATH"));
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, System.getenv("KAFKA_CREDSTORE_PASSWORD"));
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, System.getenv("KAFKA_TRUSTSTORE_PATH"));
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, System.getenv("KAFKA_CREDSTORE_PASSWORD"));
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        props.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        var username = System.getenv("KAFKA_SCHEMA_REGISTRY_USER");
        var password = System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD");

        props.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, username + ":" + password);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, System.getenv("KAFKA_SCHEMA_REGISTRY"));

        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + randomSuffixGroupID);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, groupId + inetSocketAddress.getHostString());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1000 * 60 * 10);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        var consumerFactory = consumerFactory();
        consumerFactory.addListener(new ConsumerFactory.Listener<>() {
            @Override
            public void consumerAdded(String id, Consumer<String, String> consumer) {
                log.info("Legger til consumer med id: {}", id);
            }

            @Override
            public void consumerRemoved(String id, Consumer<String, String> consumer) {
                log.warn("Fjerner consumer med id: {}. Restarter app...", id);
            }
        });
        factory.setBatchListener(true);
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(new CommonLoggingErrorHandler());
        return factory;
    }
}