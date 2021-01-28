package no.nav.registre.testnorge.oppsummeringsdokuemntservice.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.oppsummeringsdokuemntservice.domain.Oppsummeringsdokument;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.repository.OppsummeringsdokumentRepository;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.repository.model.OppsummeringsdokumentModel;

@Slf4j
@Component
@RequiredArgsConstructor
public class OppsummeringsdokumentAdapter {
    public static final String MAX_VERSION = "max_version";
    private final OppsummeringsdokumentRepository repository;
    private final ElasticsearchOperations operations;

    public void deleteAll() {
        repository.deleteAll();
    }

    public String save(Oppsummeringsdokument oppsummeringsdokument, String miljo) {
        log.info("Oppretter oppsumeringsdokuemnt i {}", miljo);
        return repository.save(oppsummeringsdokument.toModel(miljo)).getId();
    }

    public Oppsummeringsdokument get(String id) {
        return repository.findById(id).map(Oppsummeringsdokument::new).orElse(null);
    }

    public Oppsummeringsdokument getLastBy(LocalDate kalendermaaned, String orgnummer) {
        var searchQuery = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.rangeQuery("kalendermaaned")
                                .gte(kalendermaaned.withDayOfMonth(1))
                                .lte(kalendermaaned.withDayOfMonth(kalendermaaned.lengthOfMonth()))
                )
                .withQuery(
                        QueryBuilders.matchQuery("opplysningspliktigOrganisajonsnummer", orgnummer)
                )
                .addAggregation(AggregationBuilders.max(MAX_VERSION).field("version"))
                .build();
        var hits = operations.search(
                searchQuery,
                OppsummeringsdokumentModel.class
        );
        var version = getMaxValueFrom(hits.getAggregations(), MAX_VERSION);
        var models = findLastVersionByAggregation(hits, version);
        if (models.size() > 1) {
            log.warn(
                    "Fant flere en av samme versioner for kalendermaaned: {} og orgnummer: {} for version: {}. Velger den første i listen.",
                    kalendermaaned,
                    orgnummer,
                    version
            );
        }
        return models.stream().findFirst().map(Oppsummeringsdokument::new).orElse(null);
    }

    private List<OppsummeringsdokumentModel> findLastVersionByAggregation(SearchHits<OppsummeringsdokumentModel> hits, double version) {
        return hits
                .get()
                .map(SearchHit::getContent)
                .filter(model -> (long) version == model.getVersion())
                .collect(Collectors.toList());
    }

    private double getMaxValueFrom(Aggregations aggregations, String name) {
        var parsedMax = (ParsedMax) aggregations.get(name);
        return parsedMax.getValue();
    }
}
