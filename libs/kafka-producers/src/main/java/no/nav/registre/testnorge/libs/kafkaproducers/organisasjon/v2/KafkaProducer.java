package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.SneakyThrows;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class KafkaProducer<T extends SpecificRecord> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    @SneakyThrows
    public KafkaProducer(String groupId) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BROKERS"));
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, System.getenv("KAFKA_KEYSTORE_PATH"));
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, System.getenv("KAFKA_CREDSTORE_PASSWORD"));
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, System.getenv("KAFKA_TRUSTSTORE_PATH"));
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, System.getenv("KAFKA_CREDSTORE_PASSWORD"));
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");

        props.put(ProducerConfig.CLIENT_ID_CONFIG, groupId + inetSocketAddress.getHostString());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        props.put("basic.auth.credentials.source", "USER_INFO");

        var schemaRegistry = new URL(System.getenv("KAFKA_SCHEMA_REGISTRY"));
        var schemausr = System.getenv("KAFKA_SCHEMA_REGISTRY_USER");
        var schemapass = System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD");
        var schemaregusrinfo = schemausr + ":" + schemapass;

        props.put("basic.auth.user.info", schemaRegistry.getUserInfo() != null ? schemaRegistry.getUserInfo() : schemaregusrinfo);

        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, new URI(
                schemaRegistry.getProtocol(),
                null,
                schemaRegistry.getHost(),
                schemaRegistry.getPort(),
                schemaRegistry.getPath(),
                schemaRegistry.getQuery(),
                schemaRegistry.getRef()
        ).toString());

        this.kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    protected KafkaTemplate<String, T> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public abstract void send(String key, T value);
}
