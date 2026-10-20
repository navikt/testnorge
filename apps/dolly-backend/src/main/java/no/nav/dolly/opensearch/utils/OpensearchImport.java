package no.nav.dolly.opensearch.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.BrukerDTO;
import no.nav.dolly.opensearch.BestillingDokument;
import no.nav.dolly.opensearch.service.OpenSearchService;
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
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OpensearchImport implements ApplicationListener<ContextRefreshedEvent> {

    private static final String INDEX_SETTING =
            "{\"settings\":{\"index\":{\"mapping\":{\"total_fields\":{\"limit\":\"%s\"}}," +
            "\"number_of_shards\":4," +
            "\"number_of_replicas\":1}}}";
    private static final String TOTAL_FIELDS_SETTING =
            "{\"index\":{\"mapping\":{\"total_fields\":{\"limit\":\"%s\"}}}}";
    private static final int EXISTS_CHECK_CONCURRENCY = 100;
    private static final String FEILET = "Feilet";

    private final BestillingProgressRepository bestillingProgressRepository;
    private final BestillingRepository bestillingRepository;
    private final MapperFacade mapperFacade;
    private final OpenSearchService openSearchService;
    private final JsonMapper jsonMapper;
    private final BrukerServiceConsumer brukerServiceConsumer;

    @Value("${open.search.total-fields}")
    private String totalFields;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("OpenSearch database oppdatering starter ...");

        var start = System.currentTimeMillis();
        var antallLest = new AtomicInteger(0);
        var antallSkrevet = new AtomicInteger(0);

        openSearchService.indexExists()
                .flatMap(exists -> isFalse(exists) ? opprettIndexMedSetting() : oppdaterTotalFieldsSetting())
                .then(importAll(antallLest, antallSkrevet)
                        .collectList())
                .subscribe(_ ->
                        log.info("OpenSearch database oppdatering ferdig; antall lest {}, antall skrevet {}, medgått tid {} ms",
                                antallLest.get(),
                                antallSkrevet.get(),
                                System.currentTimeMillis() - start));
    }

    private Mono<String> opprettIndexMedSetting() {

        try {
            var indexSetting = INDEX_SETTING.formatted(totalFields);
            var jsonNode = jsonMapper.readTree(indexSetting);
            return openSearchService.updateIndexParams(jsonNode)
                    .doOnNext(status -> log.info("OpenSearch oppretting av indeks, status: {}", status))
                    .onErrorResume(e -> {
                        log.error("Feilet å opprette indeks med setting {}", INDEX_SETTING, e);
                        return Mono.just(FEILET);
                    });

        } catch (RuntimeException e) {
            log.error("Feilet å gjøre setting for indekser {}", INDEX_SETTING, e);
            return Mono.just(FEILET);
        }
    }

    private Mono<String> oppdaterTotalFieldsSetting() {

        try {
            var totalFieldsSetting = TOTAL_FIELDS_SETTING.formatted(totalFields);
            var jsonNode = jsonMapper.readTree(totalFieldsSetting);
            return openSearchService.updateIndexSettings(jsonNode)
                    .doOnNext(status -> log.info("OpenSearch oppdatering av total-fields for eksisterende indeks, status: {}", status))
                    .onErrorResume(e -> {
                        log.error("Feilet å oppdatere total-fields setting {} for eksisterende indeks", TOTAL_FIELDS_SETTING, e);
                        return Mono.just(FEILET);
                    });

        } catch (RuntimeException e) {
            log.error("Feilet å gjøre setting for indekser {}", TOTAL_FIELDS_SETTING, e);
            return Mono.just(FEILET);
        }
    }

    private Flux<BulkResponse> importAll(AtomicInteger antallLest, AtomicInteger antallSkrevet) {

        return brukerServiceConsumer.getAlleBrukere()
                .collect(Collectors.toMap(BrukerDTO::getId, BrukerDTO::getOrganisasjonsnummer))
                .flatMapMany(organisasjoner -> bestillingRepository.findByOrderByIdDesc()
                        .doOnNext(_ -> antallLest.incrementAndGet())
                        .flatMap(bestilling -> openSearchService.exists(bestilling.getId())
                                .filter(BooleanUtils::isNotTrue)
                                .flatMap(_ ->
                                        bestillingProgressRepository.findAllByBestillingId(bestilling.getId())
                                                .collectList()
                                                .map(progress -> {
                                                    bestilling.setProgresser(progress);
                                                    bestilling.setOrganisasjoner(organisasjoner);
                                                    return bestilling;
                                                })), EXISTS_CHECK_CONCURRENCY))
                .map(bestilling ->
                        mapperFacade.map(bestilling, BestillingDokument.class))
                .filter(bestilling -> !bestilling.isIgnore())
                .buffer(100)
                .flatMap(openSearchService::saveAll)
                .doOnNext(response -> antallSkrevet.getAndSet(antallSkrevet.get() +
                                                              response.items().size()))
                .doOnNext(_ -> {
                    if (antallSkrevet.get() % 1000 == 0) {
                        log.info("Skrevet {} bestillinger", antallSkrevet.get());
                    }
                });
    }
}