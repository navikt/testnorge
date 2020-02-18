package no.nav.registre.spion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.provider.rs.respones.SyntetiserSpionResponse;

@Slf4j
@Service
public class VedtakPublisher {

    @Value("${application.testnorgespion.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public String publish(SyntetiserSpionResponse response) {
        log.info(String.format("$$ -> Producing message --> %s", response.getMelding()));
        this.kafkaTemplate.send(topic, response.getMelding());
        return "Vedtak sent to the Kafka Topic "+ topic + " successfully";
    }
}