package no.nav.registre.testnorge.organisasjonmottak.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonmottak.service.ShutdownService;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Component
@Profile("prod")
public class KafkaConfig {

    private final String groupId;
    private final ShutdownService shutdownService;

    public KafkaConfig(@Value("${kafka.groupid}") String groupId, ShutdownService shutdownService) {
        this.groupId = groupId;
        this.shutdownService = shutdownService;
    }

    public ConsumerFactory<String, String> consumerFactory() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
        Map<String, Object> props = new HashMap<>();
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

        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
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

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        var consumerFactory = consumerFactory();
        consumerFactory.addListener(new ConsumerFactory.Listener<>() {
            @Override
            public void consumerAdded(String id, Consumer<String, String> consumer) {
                log.info("Legger til consumer med id: {}", id);
            }

            @Override
            public void consumerRemoved(String id, Consumer<String, String> consumer) {
                log.warn("Fjerner consumer med id: {}. Restarter app...", id);
                shutdownService.initiateShutdown(0);
            }
        });
        factory.setBatchListener(true);
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(new CommonLoggingErrorHandler());
        return factory;
    }
}