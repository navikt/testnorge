package no.nav.registre.populasjoner.kafka;

import static no.nav.registre.populasjoner.kafka.CollectionUtils.chunk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.populasjoner.kafka.folkeregisterperson.Folkeregisteridentifikator;
import no.nav.registre.populasjoner.kafka.folkeregisterperson.Folkeregisterperson;
import no.nav.registre.populasjoner.service.IdentService;

@Slf4j
@Component
public class PdlDokumentListener {

    private static final String JSON_NODE_IDENTIFIKATOR = "folkeregisteridentifikator";
    private static final String METADATA_OPPRETT = "OPPRETT";
    private static final String KILDE_TENOR = "TENOR"; // TODO: Denne må oppdateres når PDL vet hva som vil stå som kilde for TENOR-identer

    private final KafkaTopics kafkaTopics;
    private final ObjectMapper mapper;
    private final IdentService identService;

    @Autowired
    public PdlDokumentListener(
            KafkaTopics kafkaTopics,
            ObjectMapper mapper,
            IdentService identService
    ) {
        this.kafkaTopics = kafkaTopics;
        this.mapper = mapper;
        this.identService = identService;
    }

    @KafkaListener(topics = "#{kafkaTopics.getPdlDokument()}")
    public void onMessage(
            ConsumerRecords<String, String> records
    ) {
        log.info("Mottok melding på topic");
        var folkeregisterpersoner = KafkaUtilities.asStream(records)
                .map(this::extractPersonIdenter)
                .collect(Collectors.toList());

        var chunk = chunk(folkeregisterpersoner, 15);
        for (var personIdenter : chunk) {
            processBulk(personIdenter);
        }
    }

    private Folkeregisterperson extractPersonIdenter(ConsumerRecord<String, String> record) {
        try {
            KafkaUtilities.appendToMdc(record, true);
            if (record.value() == null) {
                return Folkeregisterperson.builder().build();
            } else {
                JsonNode node = mapper.readValue(record.value().substring(record.value().indexOf("{")), JsonNode.class).findValue(JSON_NODE_IDENTIFIKATOR);
                return Folkeregisterperson.builder().folkeregisteridentifikator(mapper.convertValue(node, new TypeReference<>() {
                })).build();
            }
        } catch (RuntimeException | IOException exception) {

            // Merk: ikke ta med exception i UnrecoverableException (den kan inneholde person opplysninger),
            // bruk MDC verdiene for å eventuelt finne meldinger som feiler (gjør unntak for preprod for feilsøking)

            log.error("Failed to process message");

            if ("preprod".equalsIgnoreCase(System.getenv("STACK"))) {
                throw new UnrecoverableException("Failed to process message", exception);
            } else {
                throw new UnrecoverableException("Failed to process message", exception);
            }
        } finally {
            MDC.clear();
        }
    }

    private void processBulk(List<Folkeregisterperson> folkeregisterpersoner) {
        for (var folkeregisterperson : folkeregisterpersoner) {
            if (folkeregisterperson.getFolkeregisteridentifikator() != null && !folkeregisterperson.getFolkeregisteridentifikator().isEmpty()) {
                findTenoridenterOgSendTilDatabase(folkeregisterperson.getFolkeregisteridentifikator());
            }
        }
    }

    private void findTenoridenterOgSendTilDatabase(List<Folkeregisteridentifikator> identifikatorer) {
        if (identifikatorer != null) {
            for (var identifikator : identifikatorer) {
                sendTenoridentTilDatabase(identifikator);
            }
        }
    }

    private void sendTenoridentTilDatabase(Folkeregisteridentifikator identifikator) {
        var endringer = identifikator.getMetadata().getEndringer();
        for (var endring : endringer) {
            if (METADATA_OPPRETT.equals(endring.getType())) {
                log.info("Ident med kilde {}", endring.getKilde());
                if (KILDE_TENOR.equals(endring.getKilde())) {
                    identService.saveIdentWithFnr(identifikator.getIdentifikasjonsnummer());
                }
                break;
            }
        }
    }
}
