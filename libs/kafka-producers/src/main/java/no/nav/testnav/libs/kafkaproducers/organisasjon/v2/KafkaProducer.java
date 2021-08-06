package no.nav.testnav.libs.kafkaproducers.organisasjon.v2;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Slf4j
abstract class KafkaProducer<T extends SpecificRecord> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    @SneakyThrows
    KafkaProducer(String groupId) {
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
        var username = System.getenv("KAFKA_SCHEMA_REGISTRY_USER");
        var password = System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD");

        props.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, username + ":" + password);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, System.getenv("KAFKA_SCHEMA_REGISTRY"));

        this.kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    protected KafkaTemplate<String, T> getKafkaTemplate() {
        return kafkaTemplate;
    }

    abstract ListenableFuture<SendResult<String, T>> send(String key, T value);
}
