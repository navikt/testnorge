package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.ElasticTyper;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final BestillingElasticRepository bestillingElasticRepository;
    private final ElasticsearchOperations searchOperations;

    public List<ElasticBestilling> getAll() {

        var all = bestillingElasticRepository.findAll();
        return StreamSupport.stream(all.spliterator(),false)
                .toList();
    }

    public List<String> getTyper(ElasticTyper[] typer) {

        var builder = QueryBuilders.boolQuery();

        Arrays.stream(typer)
                .map(this::getCriteria)
                .map(CriteriaQuery::new)
                .forEach(criteriaQuery ->  builder.must(criteriaQuery));

        var criteria = new Criteria("skjerming").exists().;

        var hits = searchOperations.search(new CriteriaQuery(criteria),
                ElasticBestilling.class, IndexCoordinates.of("bestilling"));

        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ElasticBestilling::getIdenter)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    private Criteria getCriteria(ElasticTyper type) {

        return switch(type) {
                    case AAREG -> new Criteria("aareg").exists().notEmpty();
                    case INST -> new Criteria("instdata").exists().notEmpty();
                    case KRRSTUB -> new Criteria("krrstub").exists();
                    case SIGRUN_LIGNET -> new Criteria("sigrunInntekt").exists().notEmpty();
                    case SIGRUN_PENSJONSGIVENDE -> new Criteria("sigrunPensjonsgivende").exists().notEmpty();
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
                    case ARBEIDSPLASSENCV -> new Criteria("arbeidplassencv").exists();
        };
    }
}
