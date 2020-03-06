package no.nav.registre.spion.provider.rs;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.nav.registre.spion.provider.rs.response.SyntetiserVedtakResponse;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper om = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    public int publish(List<SyntetiserVedtakResponse> syntetisertvedtakslister) throws JsonProcessingException {

        int antallVellykket = 0;
        for (int i = 0; i < syntetisertvedtakslister.size(); i++) {
            SyntetiserVedtakResponse vedtaksliste = syntetisertvedtakslister.get(i);
            log.info("Sender vedtak for person nr.{} til Kafka topic {}.", i + 1, topic);
            try {
                publish(vedtaksliste);
                log.info("Sending av vedtak for person nr. {} til Kafka Topic {} var vellykket.", i + 1, topic);
                antallVellykket++;
            } catch (ExecutionException | InterruptedException e) {
                log.error("Sending av vedtak for person nr. {} til Kafka Topic {} mislyktes.", i + 1, topic);
                continue;
            } catch (JsonProcessingException e) {
                log.error("Kunne ikke mappe vedtak til String.");
                throw e;
            }
        }

        return antallVellykket;
    }

    public void publish(SyntetiserVedtakResponse response) throws ExecutionException, InterruptedException, JsonProcessingException {
        for (Vedtak vedtak : response.getVedtak()) {
            String id = UUID.randomUUID().toString();
            String vedtakString = om.writeValueAsString(vedtak);
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    topic,
                    null,
                    id,
                    vedtakString
            );
            kafkaTemplate.send(record).get();
        }
    }
}