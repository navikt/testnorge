package no.nav.testnav.pdllagreservice.listener;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import no.nav.testnav.pdllagreservice.consumers.OpensearchDocumentService;
import no.nav.testnav.pdllagreservice.dto.OpensearchDocumentData;
import no.nav.testnav.pdllagreservice.exception.UnrecoverableException;
import no.nav.testnav.pdllagreservice.utility.CollectionUtils;
import no.nav.testnav.pdllagreservice.utility.KafkaUtilities;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static no.nav.testnav.pdllagreservice.listener.PdlAdresseMapper.createCompletionContexts;
import static no.nav.testnav.pdllagreservice.listener.PdlAdresseMapper.replaceNumberWithString;
import static no.nav.testnav.pdllagreservice.utility.MetricUtils.KAFKA_CONSUMER_TIMED;
import static no.nav.testnav.pdllagreservice.utility.MetricUtils.KEY;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class PdlAdresseListener {

    private final ObjectMapper mapper;
    private final OpensearchDocumentService service;

    @Value("${opensearch.index.adresser}")
    private String adresseIndex;


    @KafkaListener(
            id = "pdl-lagre-adresse",
            clientIdPrefix = "pdl-lagre-adresse-v3",
            topics = "pdl.adresse-v2",
            groupId = "testnav-pdl-lagre-adresse-v3",
            containerFactory = "pdlAdresseKafkaFactory"
    )
    @Timed(value = KAFKA_CONSUMER_TIMED, extraTags = {KEY, "adresse-v2"}, percentiles = {.99, .75, .50, .25})
    public void onMessage(ConsumerRecords<Long, String> records) {

        val documentList = KafkaUtilities.asStream(records)
                .map(this::convert)
                .toList();

        CollectionUtils.chunk(documentList, 15).forEach(service::processBulk);

    }

    private OpensearchDocumentData convert(ConsumerRecord<Long, String> record1) {
        try {
            KafkaUtilities.appendToMdc(record1, true);
            String json = record1.value();
            return new OpensearchDocumentData(adresseIndex, record1.key().toString(), json == null ? null : convert(json));
        } catch (RuntimeException exception) {
            throw new UnrecoverableException("Failed to process message", exception);
        } finally {
            MDC.clear();
        }
    }

    @SneakyThrows
    private Map<String, Object> convert(String json) {

        val data = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
        });
        replaceNumberWithString(data);
        createCompletionContexts(data);
        return data;
    }
}