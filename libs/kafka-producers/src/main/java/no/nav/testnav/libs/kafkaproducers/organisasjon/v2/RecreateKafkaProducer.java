package no.nav.testnav.libs.kafkaproducers.organisasjon.v2;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public abstract class RecreateKafkaProducer<T extends SpecificRecord> {
    private KafkaProducer<T> producer;

    abstract KafkaProducer<T> create();

    public RecreateKafkaProducer() {
        this.producer = create();
    }

    public void send(String key, T value) {
        producer.send(key, value).addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                if (ex instanceof TopicAuthorizationException) {
                    log.info("Roterer passord for kafka.", ex);
                    producer = create();
                    producer.send(key, value);
                } else {
                    log.error("Uventet feil ved opprettelse av uuid: {}", key, ex);
                }
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                log.info("Melding send med uuid: {}", key);
            }
        });
    }
}
