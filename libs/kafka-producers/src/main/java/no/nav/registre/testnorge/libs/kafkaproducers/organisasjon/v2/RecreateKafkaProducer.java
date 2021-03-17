package no.nav.registre.testnorge.libs.kafkaproducers.organisasjon.v2;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.TopicAuthorizationException;

@Slf4j
public abstract class RecreateKafkaProducer<T extends SpecificRecord> {
    private KafkaProducer<T> producer;

    abstract KafkaProducer<T> create();

    public RecreateKafkaProducer() {
        this.producer = create();
    }

    public void send(String key, T value) {
        try {
            producer.send(key, value);
        } catch (TopicAuthorizationException e) {
            log.info("Roterer passord for kafka.", e);
            producer = create();
            producer.send(key, value);
        }
    }

}
