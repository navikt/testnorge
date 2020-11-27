package no.nav.registre.testnorge.organisasjonmottak.consumer.kafka;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public abstract class KafkaProducer<T extends SpecificRecord> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    public KafkaProducer(String bootstrapAddress, String groupId, String schemaregistryServers, String username, String password) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaregistryServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, groupId + inetSocketAddress.getHostString());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=" + username + " password=" + password + ";");

        String navTruststorePath = System.getenv("NAV_TRUSTSTORE_PATH");
        if (navTruststorePath != null) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, new File(navTruststorePath).getAbsolutePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, System.getenv("NAV_TRUSTSTORE_PASSWORD"));
        }
        this.kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    KafkaTemplate<String, T> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public abstract void send(String key, T value);
}
