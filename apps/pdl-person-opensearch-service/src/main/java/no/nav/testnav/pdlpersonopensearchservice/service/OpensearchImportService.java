package no.nav.testnav.pdlpersonopensearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.pdlpersonopensearchservice.consumers.ElasticParamsConsumer;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OpensearchImportService {

    private static final String INDEX_SETTING =
            "{\"settings\":{\"index\":{\"mapping\":{\"total_fields\":{\"limit\":\"%s\"}}}}}";

    @Value("${open.search.total-fields}")
    private String totalFields;

    private final MapperFacade mapperFacade;
    private final ElasticParamsConsumer elasticParamsConsumer;
    private final ObjectMapper objectMapper;

    private Mono<String> oppdaterIndexSetting() {

        try {
            var indexSetting = String.format(INDEX_SETTING, totalFields);
            var jsonFactory = objectMapper.getFactory();
            var jsonParser = jsonFactory.createParser(indexSetting);
            var jsonNode = (JsonNode) objectMapper.readTree(jsonParser);
            return elasticParamsConsumer.oppdaterParametre(jsonNode)
                    .doOnNext(status -> log.info("OpenSearch oppdatering av indeks, status: {}", status));

        } catch (IOException e) {
            log.error("Feilet å gjøre setting for indekser {}", INDEX_SETTING, e);
            return Mono.just("Feilet");
        }
    }

    private Flux<ElasticBestilling> importAll(AtomicInteger antallLest, AtomicInteger antallSkrevet) {

        return bestillingRepository.findBy()
                .sort(Comparator.comparing(Bestilling::getId).reversed())
                .doOnNext(bestilling -> antallLest.incrementAndGet())
                .flatMap(bestilling -> hasBestilling(bestilling.getId())
                        .zipWith(Mono.just(bestilling)))
                .takeWhile(tuple -> BooleanUtils.isNotTrue(tuple.getT1()))
                .flatMap(tuple ->
                        bestillingProgressRepository.findAllByBestillingId(tuple.getT2().getId())
                                .collectList()
                                .map(progress -> {
                                    tuple.getT2().setProgresser(progress);
                                    return tuple.getT2();
                                }))
                .map(bestilling ->
                        mapperFacade.map(bestilling, ElasticBestilling.class))
                .filter(bestilling -> !bestilling.isIgnore())
                .flatMap(this::save)
                .doOnNext(bestilling -> antallSkrevet.incrementAndGet())
                .doOnNext(bestilling -> {
                    if (antallSkrevet.get() % 1000 == 0) {
                        log.info("Skrevet {} bestillinger", antallSkrevet.get());
                    }
                });
    }

    private Mono<Boolean> hasBestilling(Long id) {

        return Mono.just(bestillingElasticRepository.existsById(id));
    }

    private Mono<ElasticBestilling> save(ElasticBestilling elasticBestilling) {

        try {
            return Mono.just(bestillingElasticRepository.save(elasticBestilling));

        } catch (UncategorizedElasticsearchException e) {

            log.warn("Feilet å lagre elastic id {}, {}", elasticBestilling.getId(), e.getLocalizedMessage());
            return Mono.empty();
        }
    }
}