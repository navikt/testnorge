package no.nav.registre.spion.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.domain.Vedtak;
import no.nav.registre.spion.provider.rs.response.SyntetiserSpionResponse;

@Slf4j
@Service
public class VedtakPublisher {

    @Value("${application.testnorgespion.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, List<Vedtak>> kafkaTemplate;

    public SyntetiserSpionResponse publish(List<Vedtak> vedtak) {
        log.info("Sender vedtak til Kafka Topic {}.", topic);
        try{
            this.kafkaTemplate.send(topic, vedtak);
            log.info("Sending av vedtak til Kafka Topic {} var vellykket.", topic);
            return new SyntetiserSpionResponse("");
        }catch(Exception e){
            log.error("Sending av vedtak til Kafka Topic {} mislyktes.", topic);
            return new SyntetiserSpionResponse("");
        }
    }
}