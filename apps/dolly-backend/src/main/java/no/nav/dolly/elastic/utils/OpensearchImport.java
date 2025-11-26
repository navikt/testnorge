package no.nav.dolly.elastic.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.elastic.BestillingDokument;
import no.nav.dolly.elastic.service.OpenSearchService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OpensearchImport implements ApplicationListener<ContextRefreshedEvent> {

    private static final String INDEX_SETTING =
            "{\"settings\":{\"index\":{\"mapping\":{\"total_fields\":{\"limit\":\"%s\"}}," +
                    "\"number_of_shards\":4," +
                    "\"number_of_replicas\":1}}}";

    private final BestillingProgressRepository bestillingProgressRepository;
    private final BestillingRepository bestillingRepository;
    private final MapperFacade mapperFacade;
    private final OpenSearchService openSearchService;
    private final ObjectMapper objectMapper;

    @Value("${open.search.total-fields}")
    private String totalFields;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("OpenSearch database oppdatering starter ...");

        var start = System.currentTimeMillis();
        var antallLest = new AtomicInteger(0);
        var antallSkrevet = new AtomicInteger(0);

        openSearchService.indexExists()
                .flatMap(exists -> isFalse(exists) ? oppdaterIndexSetting() : Mono.empty())
                .then(importAll(antallLest, antallSkrevet)
                        .collectList())
                .subscribe(bestillinger ->
                        log.info("OpenSearch database oppdatering ferdig; antall lest {}, antall skrevet {}, medgått tid {} ms",
                                antallLest.get(),
                                antallSkrevet.get(),
                                System.currentTimeMillis() - start));
    }

    private Mono<String> oppdaterIndexSetting() {

        try {
            var indexSetting = String.format(INDEX_SETTING, totalFields);
            var jsonNode = objectMapper.readTree(indexSetting);
            return openSearchService.updateIndexParams(jsonNode)
                    .doOnNext(status -> log.info("OpenSearch oppdatering av indeks, status: {}", status));

        } catch (IOException e) {
            log.error("Feilet å gjøre setting for indekser {}", INDEX_SETTING, e);
            return Mono.just("Feilet");
        }
    }

    private Flux<BulkResponse> importAll(AtomicInteger antallLest, AtomicInteger antallSkrevet) {

        return bestillingRepository.findByOrderByIdDesc()
                .doOnNext(bestilling -> antallLest.incrementAndGet())
                .filter(bestilling -> isNotBlank(bestilling.getBestKriterier()) &&
                        !"{}".equals(bestilling.getBestKriterier()))
                .flatMap(bestilling -> openSearchService.exists(bestilling.getId())
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
                        mapperFacade.map(bestilling, BestillingDokument.class))
                .filter(bestilling -> !bestilling.isIgnore())
                .buffer(100)
                .flatMap(openSearchService::saveAll)
                .doOnNext(response -> antallSkrevet.getAndSet(antallSkrevet.get() +
                        response.items().size()))
                .doOnNext(bestilling -> {
                    if (antallSkrevet.get() % 1000 == 0) {
                        log.info("Skrevet {} bestillinger", antallSkrevet.get());
                    }
                });
    }
}