package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2;

import static io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig.SCHEMA_REGISTRY_USER_INFO_CONFIG;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class KafkaProducer<T extends SpecificRecord> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    @SneakyThrows
    public KafkaProducer(String groupId, String proxy) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
        var keystorePassword = System.getenv("KAFKA_CREDSTORE_PASSWORD");
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BROKERS"));
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, System.getenv("KAFKA_KEYSTORE_PATH"));
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, System.getenv("KAFKA_TRUSTSTORE_PATH"));
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, keystorePassword);
        props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "jks");
        props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12");
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");

        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

        props.put(ProducerConfig.CLIENT_ID_CONFIG, groupId + inetSocketAddress.getHostString());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        props.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
//
//        var username = System.getenv("KAFKA_SCHEMA_REGISTRY_USER");
//        var password = System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD");
//
//        props.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, username + ":" + password);
//        var kafka_schema_registry = System.getenv("KAFKA_SCHEMA_REGISTRY");
//        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafka_schema_registry);

//        log.info("kafka_schema_registry: {}", kafka_schema_registry);
        this.kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    protected KafkaTemplate<String, T> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public abstract void send(String key, T value);
}
