package no.nav.registre.spion.provider.rs.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.domain.Vedtak;

@Slf4j
@Component
public class VedtakPublisher {

    @Value("${application.kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, Vedtak> kafkaTemplate;

    @Transactional
    public void publish(Vedtak vedtak) throws ExecutionException, InterruptedException {

        String id = UUID.randomUUID().toString();
        ProducerRecord<String, Vedtak> record = new ProducerRecord<>(
                topic,
                null,
                id,
                vedtak
        );

        kafkaTemplate.send(record).get();

    }
}