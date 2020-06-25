package no.nav.registre.populasjoner.config;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

import no.nav.common.utils.Credentials;
import no.nav.registre.populasjoner.kafka.DocumentIdWrapper;
import no.nav.registre.populasjoner.kafka.KafkaHelsesjekk;
import no.nav.registre.populasjoner.kafka.KafkaTopics;

@EnableKafka
@Configuration
public class KafkaConfig {

    private final KafkaHelsesjekk kafkaHelsesjekk;

    private final Credentials serviceUserCredentials;

    @Value("${spring.kafka.bootstrap-servers}")
    private String brokersUrl;

    @Autowired
    public KafkaConfig(
            KafkaHelsesjekk kafkaHelsesjekk,
            Credentials serviceUserCredentials
    ) {
        this.kafkaHelsesjekk = kafkaHelsesjekk;
        this.serviceUserCredentials = serviceUserCredentials;
    }

    @Bean
    public KafkaTopics kafkaTopics() {
        return KafkaTopics.create();
    }

    @Bean
    public KafkaListenerContainerFactory kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaConsumerProperties(brokersUrl, serviceUserCredentials)));
        factory.setErrorHandler(kafkaHelsesjekk);
        return factory;
    }

    private HashMap<String, Object> kafkaBaseProperties(
            String kafkaBrokersUrl,
            Credentials serviceUserCredentials
    ) {
        JsonDeserializer<DocumentIdWrapper> deserializer = new JsonDeserializer<>(DocumentIdWrapper.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        HashMap<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, kafkaBrokersUrl);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + serviceUserCredentials.username + "\" password=\"" + serviceUserCredentials.password + "\";");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        return props;
    }

    private HashMap<String, Object> kafkaConsumerProperties(
            String kafkaBrokersUrl,
            Credentials serviceUserCredentials
    ) {
        HashMap<String, Object> props = kafkaBaseProperties(kafkaBrokersUrl, serviceUserCredentials);
        props.put(GROUP_ID_CONFIG, "testnorge-populasjoner-default-pdlDokumenter-1");
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(MAX_POLL_INTERVAL_MS_CONFIG, 5000);
        return props;
    }
}
