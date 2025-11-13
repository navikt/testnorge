package no.nav.testnav.pdllagreservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.pdllagreservice.consumers.OpensearchDocumentService;
import no.nav.testnav.pdllagreservice.dto.OpensearchDocumentData;
import no.nav.testnav.pdllagreservice.utility.CollectionUtils;
import no.nav.testnav.pdllagreservice.utility.KafkaUtilities;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

import static java.util.Objects.isNull;
import static no.nav.testnav.pdllagreservice.utility.MetricUtils.KAFKA_CONSUMER_TIMED;
import static no.nav.testnav.pdllagreservice.utility.MetricUtils.KEY;


@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class PdlPersonDokumentListener {

    private final OpensearchDocumentService service;
    private final ObjectMapper mapper;

    @Value("${opensearch.index.personer}")
    private String personIndex;

    @KafkaListener(
            id = "pdl-lagre-person",
            clientIdPrefix = "testnav-lagre-person",
            topics = "pdl.pdl-persondokument-tagged-v1",
            groupId = "testnav-pdl-lagre-person-v2",
            containerFactory = "pdlDokumentKafkaFactory"
    )
    @Timed(value = KAFKA_CONSUMER_TIMED, extraTags = {KEY, "pdldokument"}, percentiles = {.99, .75, .50, .25})
    public void onMessage(ConsumerRecords<String, String> records) {

        log.info("Received {} records", records.count());

        val documentList = KafkaUtilities.asStream(records)
                .map(this::convert)
                .toList();

        CollectionUtils.chunk(documentList, 15).forEach(service::processBulk);
    }

    private OpensearchDocumentData convert(ConsumerRecord<String, String> post) {
        try {
            KafkaUtilities.appendToMdc(post, true);

            if (isNull(post.value())) {
                return new OpensearchDocumentData(personIndex, post.key(), null);
            } else {
                val dokument = mapper.readValue(post.value(), HashMap.class);
                return new OpensearchDocumentData(personIndex, post.key(), dokument);
            }
        } catch (RuntimeException | IOException exception) {

            // Merk: ikke ta med exception i UnrecoverableException (den kan inneholde person opplysninger),
            // bruk MDC verdiene for å eventuelt finne meldinger som feiler (gjør unntak for preprod for feilsøking)

            log.error("Failed to process message: key: {}, value: {}", post.key(), post.value(), exception);

            return new OpensearchDocumentData(personIndex, post.key(), null);
        } finally {
            MDC.clear();
        }
    }
}

