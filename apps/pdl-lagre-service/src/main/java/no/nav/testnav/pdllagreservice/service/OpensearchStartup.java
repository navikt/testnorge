package no.nav.testnav.pdllagreservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.pdllagreservice.consumers.OpensearchParamsConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OpensearchStartup implements ApplicationListener<ContextRefreshedEvent> {

    private static final String INDEX_SETTING =
            "{\"settings\":{\"index\":{\"mapping\":{\"total_fields\":{\"limit\":\"%s\"}}}}}";

    @Value("${opensearch.total-fields}")
    private String totalFields;

    @Value("${opensearch.index.personer}")
    private String personIndex;

    @Value("${opensearch.index.adresser}")
    private String adresseIndex;

    private final OpensearchParamsConsumer opensearchParamsConsumer;
    private final ObjectMapper objectMapper;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("OpenSearch database oppdatering starter ...");

        Mono.zip(updateIndexSetting(personIndex),
                        updateIndexSetting(adresseIndex))
                .subscribe(status ->
                        log.info("OpenSearch database oppdatering ferdig"));
    }

    private Mono<String> updateIndexSetting(String index) {

        try {
            val indexSetting = INDEX_SETTING.formatted(totalFields);
            val command = objectMapper.readTree(indexSetting);

            return opensearchParamsConsumer.oppdaterParametre(command, index)
                    .doOnNext(status -> log.info("OpenSearch oppdatering av indeks, status: {}", status));

        } catch (IOException e) {

            log.error("Feilet å gjøre setting av {} for index {}", INDEX_SETTING, index, e);
            return Mono.just("Feilet");
        }
    }
}