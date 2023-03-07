package no.nav.testnav.libs.kafkaproducers.organisasjon.v2;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.TopicAuthorizationException;

import static java.util.Objects.nonNull;

@Slf4j
public abstract class RecreateKafkaProducer<T extends SpecificRecord> {
    private KafkaProducer<T> producer;

    public RecreateKafkaProducer() {
        this.producer = create();
    }

    public void send(String key, T value) {
        producer.send(key, value).whenComplete((stringTSendResult, throwable) -> {
            if (nonNull(throwable)) {
                if (throwable instanceof TopicAuthorizationException) {
                    log.info("Roterer passord for kafka.", throwable);
                    producer = create();
                    producer.send(key, value);
                } else {
                    log.error("Uventet feil ved opprettelse av uuid: {}", key, throwable);
                }
                log.info("Melding sendt med uuid: {}", key);
            }
        });
    }

    abstract KafkaProducer<T> create();
}
