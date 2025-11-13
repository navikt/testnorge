package no.nav.testnav.pdllagreservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.pdllagreservice.consumers.OpensearchParamsConsumer;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OpensearchStartup {

    private static final String INDEX_SETTING =
            "{\"settings\":{\"index\":{\"mapping\":{\"total_fields\":{\"limit\":\"%s\"}}}}}";

    @Value("${opensearch.total-fields}")
    private String totalFields;

    @Value("${opensearch.index.personer}")
    private String personIndex;

    @Value("${opensearch.index.adresser}")
    private String adresserIndex;

    private final OpensearchParamsConsumer opensearchParamsConsumer;
    private final ObjectMapper objectMapper;
    private final OpenSearchClient openSearchClient;

    @PostConstruct
    public void opensearchStartup() {

        log.info("OpenSearch database oppdatering starter ...");

        updateIndexSetting(personIndex)
                .then(updateIndexSetting(adresserIndex))
                .then(Mono.fromRunnable(() -> log.info("OpenSearch database oppdatering ferdig")))
                .block();
    }

    private Mono<String> updateIndexSetting(String index) {

        try {
            val indexSetting = INDEX_SETTING.formatted(totalFields);
            val command = objectMapper.readTree(indexSetting);

            val existResponse = openSearchClient.indices().exists(i -> i.index(index));
            if (!existResponse.value()) {
                openSearchClient.indices().create(i -> i.index(index));
                log.warn("Opprettet ny index: {}", index);
            }

            return opensearchParamsConsumer.oppdaterParametre(command, index)
                    .doOnNext(status -> log.info("OpenSearch oppdatering av indeks, status: {}", status));

        } catch (IOException e) {

            log.error("Feilet å gjøre setting av {} for index {}", INDEX_SETTING, index, e);
            return Mono.just("Feilet");
        }
    }
}