package no.nav.dolly.elastic.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.consumer.ElasticParamsConsumer;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OpensearchImport implements ApplicationListener<ContextRefreshedEvent> {

    private static final String INDEX_SETTING =
            "{\"settings\":{\"index\":{\"mapping\":{\"total_fields\":{\"limit\":\"%s\"}}}}}";

    @Value("${open.search.total-fields}")
    private String totalFields;

    private final BestillingRepository bestillingRepository;
    private final BestillingElasticRepository bestillingElasticRepository;
    private final MapperFacade mapperFacade;
    private final ElasticParamsConsumer elasticParamsConsumer;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("OpenSearch database oppdatering starter ...");

        var start = System.currentTimeMillis();
        var antallLest = new AtomicInteger(0);
        var antallSkrevet = new AtomicInteger(0);
        oppdaterIndexSetting();

        importAll(antallLest, antallSkrevet);

        log.info("OpenSearch database oppdatering ferdig; antall lest {}, antall skrevet {}, medgått tid {} ms",
                antallLest.get(),
                antallSkrevet.get(),
                System.currentTimeMillis() - start);
    }

    private void oppdaterIndexSetting() {

        try {
            var indexSetting = String.format(INDEX_SETTING, totalFields);
            var jsonFactory = objectMapper.getFactory();
            var jsonParser = jsonFactory.createParser(indexSetting);
            var jsonNode = (JsonNode) objectMapper.readTree(jsonParser);
            elasticParamsConsumer.oppdaterParametre(jsonNode)
                    .subscribe(status -> log.info("OpenSearch oppdatering av indeks, status: {}", status));

        } catch (IOException e) {
            log.error("Feilet å gjøre setting for indekser {}", INDEX_SETTING, e);
        }
    }

    private void importAll(AtomicInteger antallLest, AtomicInteger antallSkrevet) {

        Flux.fromIterable(bestillingRepository.findAll())
                .sort(Comparator.comparing(Bestilling::getId).reversed())
                .doOnNext(bestilling -> antallLest.incrementAndGet())
                .takeWhile(bestilling -> hasNotBestilling(bestilling.getId()))
                .map(bestilling -> mapperFacade.map(bestilling, ElasticBestilling.class))
                .filter(bestilling -> !bestilling.isIgnore())
                .doOnNext(this::save)
                .doOnNext(bestilling -> antallSkrevet.incrementAndGet())
                .doOnNext(bestilling -> {
                    if (antallSkrevet.get() % 1000 == 0) {
                        log.info("Skrevet {} bestillinger", antallSkrevet.get());
                    }
                })
                .subscribe();
    }

    private boolean hasNotBestilling(Long id) {

        return !bestillingElasticRepository.existsById(id);
    }

    private void save(ElasticBestilling elasticBestilling) {

        try {
            bestillingElasticRepository.save(elasticBestilling);

        } catch (UncategorizedElasticsearchException e) {

            log.warn("Feilet å lagre elastic id {}, {}", elasticBestilling.getId(), e.getLocalizedMessage());
        }
    }
}