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
import org.opensearch.client.RestHighLevelClient;
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

    private static final String TOTAL_FIELDS = "index.mapping.total_fields.limit";

    private final BestillingRepository bestillingRepository;
    private final BestillingElasticRepository bestillingElasticRepository;
    private final MapperFacade mapperFacade;
    private final RestHighLevelClient restHighLevelClient;
    private final ElasticParamsConsumer elasticParamsConsumer;
    private final ObjectMapper objectMapper;

    @Value("${opensearch.total-fields}")
    private String totalFields;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        var jsonString  = """
                {\"settings\": {
                    \"index\" : {
                      \"mapping\" : {
                        \"total_fields\" : {
                          \"limit\" : \"1500\"
                        }
                      }
                    }
                }} """;

        try {
            var jsonFactory = objectMapper.getFactory();
            var jsonParser = jsonFactory.createParser(jsonString);
            var jsonNode = (JsonNode) objectMapper.readTree(jsonParser);
            elasticParamsConsumer.oppdaterParametre(jsonNode)
                    .subscribe(status -> log.info("Status fra parameter oppdatering: {}", status));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        try {
//            var indexRequest = new CreateIndexRequest.Builder()
//                    .index("bestillinger")
//                    .settings(IndexSettings.Builder)
//                    .build();
//            var request = new UpdateSettingsRequest();
//            request.settings(Settings.builder()
//                    .put(TOTAL_FIELDS, totalFields)
//                    .build());
//            restHighLevelClient.indices()
//                    .create()
//                    .putSettings(request, RequestOptions.DEFAULT);
//
//        } catch (OpenSearchException | IOException e) {
//            log.error("Feilet å sette {} for samtlige indekser", TOTAL_FIELDS, e);
//        }

        var start = System.currentTimeMillis();
        var antallLest = new AtomicInteger(0);
        var antallSkrevet = new AtomicInteger(0);
        log.info("OpenSearch database oppdatering starter ...");

        importAll(antallLest, antallSkrevet);

        log.info("OpenSearch database oppdatering ferdig; antall lest {}, antall skrevet {}, medgått tid {} ms",
                antallLest.get(),
                antallSkrevet.get(),
                System.currentTimeMillis() - start);
    }

    private void importAll(AtomicInteger antallLest, AtomicInteger antallSkrevet) {

        Flux.fromIterable(bestillingRepository.findAll())
                .sort(Comparator.comparing(Bestilling::getId).reversed())
                .doOnNext(bestilling -> antallLest.incrementAndGet())
                .takeWhile(bestilling -> hasNotBestilling(bestilling.getId()))
                .map(bestilling -> mapperFacade.map(bestilling, ElasticBestilling.class))
                .filter(bestilling -> !bestilling.isIgnore())
                .doOnNext(bestilling -> antallSkrevet.incrementAndGet())
                .doOnNext(this::save)
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