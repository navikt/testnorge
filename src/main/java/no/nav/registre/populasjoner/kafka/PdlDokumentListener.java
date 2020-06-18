package no.nav.registre.populasjoner.kafka;

import static no.nav.registre.populasjoner.kafka.CollectionUtils.chunk;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.populasjoner.kafka.domain.PdlDokument;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdlDokumentListener {

    private final ObjectMapper mapper;
    private final PdlDokumentService service;

    @KafkaListener(topics = "${pdl.kafka.topics.pdlDokument-v1.name}", groupId = "${app.kafka.group-id-prefix}-pdlDokumenter-1")
    public void onMessage(ConsumerRecords<String, String> records) {
        log.info("Mottok melding på topic");

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

