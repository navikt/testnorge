package no.nav.registre.populasjoner.kafka;

import static no.nav.registre.populasjoner.kafka.CollectionUtils.chunk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.populasjoner.kafka.domain.PdlDokument;

@Slf4j
@Component
public class PdlDokumentListener {

    private final KafkaTopics kafkaTopics;
    private final ObjectMapper mapper;
    private final PdlDokumentService service;

    @Autowired
    public PdlDokumentListener(
            KafkaTopics kafkaTopics,
            ObjectMapper mapper,
            PdlDokumentService service
    ) {
        this.kafkaTopics = kafkaTopics;
        this.mapper = mapper;
        this.service = service;
    }

    @KafkaListener(topics = "#{kafkaTopics.getPdlDokument()}")
    public void onMessage(@Payload String message) {
        log.info("Mottok melding på topic");
        log.info(message);

        ConsumerRecords<String, String> records = new ObjectMapper().convertValue(message, new TypeReference<>() {
        });

        List<DocumentIdWrapper> documentList = KafkaUtilities.asStream(records)
                .map(this::convert)
                .collect(Collectors.toList());

        Collection<List<DocumentIdWrapper>> chunk = chunk(documentList, 15);
        for (List<DocumentIdWrapper> documentIdWrappers : chunk) {
            service.processBulk(documentIdWrappers);
        }
    }

    private DocumentIdWrapper convert(ConsumerRecord<String, String> record) {
        try {
            KafkaUtilities.appendToMdc(record, true);
            if (record.value() == null) {
                return new DocumentIdWrapper(record.key(), null);
            } else {
                val dokument = mapper.readValue(record.value(), PdlDokument.class);
                return new DocumentIdWrapper(record.key(), dokument);
            }
        } catch (RuntimeException | IOException exception) {

            // Merk: ikke ta med exception i UnrecoverableException (den kan inneholde person opplysninger),
            // bruk MDC verdiene for å eventuelt finne meldinger som feiler (gjør unntak for preprod for feilsøking)

            log.error("Failed to process message");

            if ("preprod".equalsIgnoreCase(System.getenv("STACK"))) {
                throw new UnrecoverableException("Failed to process message", exception);
            } else {
                throw new UnrecoverableException("Failed to process message");
            }
        } finally {
            MDC.clear();
        }
    }
}
