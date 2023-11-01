package no.nav.dolly.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final BestillingElasticRepository bestillingElasticRepository;
    private final ElasticsearchOperations searchOperations;
    private Random random = new SecureRandom();

    public List<ElasticBestilling> getAll() {

        var all = bestillingElasticRepository.findAll();
        return StreamSupport.stream(all.spliterator(),false)
                .toList();
    }

    public List<String> getTyper(ElasticTyper[] typer) {

        var criteria = new Criteria();

        Arrays.stream(typer)
                .map(this::getCriteria)
                .forEach(criteria::and);

//        var query = new CriteriaQuery(criteria);

        var functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(new RandomScoreFunctionBuilder().seed(random.nextLong()));

        var query = QueryBuilder.getSearchRequest(QueryBuilder.buildPersonSearchQuery(), 1, 10, 10)

//        var hits = searchOperations.search(query,
//                ElasticBestilling.class, IndexCoordinates.of("bestilling"));

        var hits = searchOperations.search(query)

        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ElasticBestilling::getIdenter)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    private Criteria getCriteria(ElasticTyper type) {

        return switch(type) {
                    case AAREG -> new Criteria("aareg").exists();
                    case INST -> new Criteria("instdata").exists();
                    case KRRSTUB -> new Criteria("krrstub").exists();
                    case SIGRUN_LIGNET -> new Criteria("sigrunInntekt").exists();
                    case SIGRUN_PENSJONSGIVENDE -> new Criteria("sigrunPensjonsgivende").exists();
                    case ARENA_BRUKER -> new Criteria("arenaBruker").exists();
                    case ARENA_AAP -> new Criteria("arenaAap").exists();
                    case ARENA_AAP115 -> new Criteria("arenaAap115").exists();
                    case ARENA_DAGP -> new Criteria("arenaDagpenger").exists();
                    case UDISTUB -> new Criteria("udistub").exists();
                    case INNTK -> new Criteria("inntektstub").exists();
                    case PEN_INNTEKT -> new Criteria("penInntekt").exists();
                    case PEN_TP -> new Criteria("penTp").exists();
                    case PEN_AP -> new Criteria("penAlderspensjon").exists();
                    case PEN_UT -> new Criteria("penUforetrygd").exists();
                    case INNTKMELD -> new Criteria("inntektsmelding").exists();
                    case BRREGSTUB -> new Criteria("brregstub").exists();
                    case DOKARKIV -> new Criteria("dokarkiv").exists();
                    case MEDL -> new Criteria("medl").exists();
                    case HISTARK -> new Criteria("histark").exists();
                    case SYKEMELDING -> new Criteria("sykemelding").exists();
                    case SKJERMING -> new Criteria("skjerming").exists();
                    case PDLPERSON -> new Criteria("pdldata").exists();
                    case BANKKONTO -> new Criteria("bankkonto").exists();
                    case ARBEIDSPLASSENCV -> new Criteria("arbeidsplassenCV").exists();
        };
    }

    private static void addRandomScoreQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getRandomSeed())
                .ifPresent(value -> {
                    if (!value.isEmpty()) {
                        queryBuilder.must(QueryBuilders.functionScoreQuery(new RandomScoreFunctionBuilder().seed(value)));
                    }
                });
    }
}
