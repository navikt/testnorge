package no.nav.registre.testnorge.organisasjonmottak.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
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
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Component
@Profile("prod")
@RequiredArgsConstructor
public class KafkaConfig {
    @Value("${kafka.groupid}")
    private String groupId;

    @SneakyThrows
    public ConsumerFactory<String, String> consumerFactory() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BROKERS"));
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, System.getenv("KAFKA_KEYSTORE_PATH"));
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, System.getenv("KAFKA_CREDSTORE_PASSWORD"));
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, System.getenv("KAFKA_TRUSTSTORE_PATH"));
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, System.getenv("KAFKA_CREDSTORE_PASSWORD"));
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put("basic.auth.credentials.source", "USER_INFO");

        var schemaRegistry = new URL(System.getenv("KAFKA_SCHEMA_REGISTRY"));
        var schemausr = System.getenv("KAFKA_SCHEMA_REGISTRY_USER");
        var schemapass = System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD");
        var schemaregusrinfo = schemausr + ":" + schemapass;

        props.put("basic.auth.user.info", schemaRegistry.getUserInfo() != null ? schemaRegistry.getUserInfo() : schemaregusrinfo);

        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, groupId + inetSocketAddress.getHostString());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1000 * 60 * 10);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, new URI(
                schemaRegistry.getProtocol(),
                null,
                schemaRegistry.getHost(),
                schemaRegistry.getPort(),
                schemaRegistry.getPath(),
                schemaRegistry.getQuery(),
                schemaRegistry.getRef()
        ).toString());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setErrorHandler(new SeekToCurrentErrorHandler(
                (consumer, exception) -> log.error("Klarer ikke å opprette bestilling med uuid: {}", consumer.key()),
                new FixedBackOff(30 * 1000, 3)
        ));
        return factory;
    }
}