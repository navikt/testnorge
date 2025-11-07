package no.nav.testnav.pdldatalagreservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.pdldatalagreservice.exception.UnrecoverableException;
import no.nav.testnav.pdldatalagreservice.service.OpensearchDocumentData;
import no.nav.testnav.pdldatalagreservice.service.OpensearchDocumentService;
import no.nav.testnav.pdldatalagreservice.utility.CollectionUtils;
import no.nav.testnav.pdldatalagreservice.utility.KafkaUtilities;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.testnav.pdldatalagreservice.utility.MetricUtils.KAFKA_CONSUMER_TIMED;
import static no.nav.testnav.pdldatalagreservice.utility.MetricUtils.KEY;


@Slf4j
@Component
@Profile("kafka")
@RequiredArgsConstructor
public class PdlPersonDokumentListener {

    private final OpensearchDocumentService service;
    private final ObjectMapper mapper;

    @Value("${open.search.index}")
    private String personIndex;

    @KafkaListener(
            id = "pdl-sok",
            clientIdPrefix = "pdl-sok-legacy-person",
            topics = "${app.kafka.topic.persondokument.name}",
            groupId = "${app.kafka.group-id-prefix}-pdlDokumenter-v1",
            containerFactory = "pdlDokumentKafkaFactory"
    )
    @Timed(value = KAFKA_CONSUMER_TIMED, extraTags = {KEY, "pdldokument"}, percentiles = {.99, .75, .50, .25})
    public void onMessage(ConsumerRecords<String, String> records) {
        val documentList = KafkaUtilities.asStream(records)
                .map(this::convert)
                .collect(Collectors.toList());

        CollectionUtils.chunk(documentList, 15).forEach(service::processBulk);
    }

    private OpensearchDocumentData convert(ConsumerRecord<String, String> record) {
        try {
            KafkaUtilities.appendToMdc(record, true);

            if (isNull(record.value())) {
                return new OpensearchDocumentData(personIndex, record.key(), null);
            } else {
                val dokument = mapper.readValue(record.value(), HashMap.class);
                return new OpensearchDocumentData(personIndex, record.key(), dokument);
            }
        } catch (RuntimeException | IOException exception) {

            // Merk: ikke ta med exception i UnrecoverableException (den kan inneholde person opplysninger),
            // bruk MDC verdiene for å eventuelt finne meldinger som feiler (gjør unntak for preprod for feilsøking)

            log.error("Failed to process message");

                throw new UnrecoverableException("Failed to process message", exception);
        } finally {
            MDC.clear();
        }
    }
}

