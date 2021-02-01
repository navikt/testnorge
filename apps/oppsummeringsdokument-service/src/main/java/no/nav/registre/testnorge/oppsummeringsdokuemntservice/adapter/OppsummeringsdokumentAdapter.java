package no.nav.registre.testnorge.oppsummeringsdokuemntservice.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.oppsummeringsdokuemntservice.consumer.AaregSyntConsumer;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.domain.Oppsummeringsdokument;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.repository.OppsummeringsdokumentRepository;
import no.nav.registre.testnorge.oppsummeringsdokuemntservice.repository.model.OppsummeringsdokumentModel;

@Slf4j
@Component
@RequiredArgsConstructor
public class OppsummeringsdokumentAdapter {
    private final OppsummeringsdokumentRepository repository;
    private final ElasticsearchOperations operations;
    private final AaregSyntConsumer aaregSyntConsumer;

    public void deleteAll() {
        repository.deleteAll();
    }

    public String save(Oppsummeringsdokument oppsummeringsdokument, String miljo, String origin) {
        log.info("Oppretter oppsumeringsdokuemnt i {}", miljo);
        aaregSyntConsumer.saveOpplysningspliktig(oppsummeringsdokument);
        return repository.save(oppsummeringsdokument.toModel(miljo, origin)).getId();
    }

    public Oppsummeringsdokument get(String id) {
        return repository.findById(id).map(Oppsummeringsdokument::new).orElse(null);
    }

    private Page<Oppsummeringsdokument> getAllCurrentDocumentsBy(NativeSearchQueryBuilder builder, Pageable pageable) {

        builder.withSort(SortBuilders.fieldSort("lastModified").order(SortOrder.ASC));
        var searchHist = operations.search(
                builder.build(),
                OppsummeringsdokumentModel.class
        );

        var list = searchHist.get().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(
                getAllCurrentDocumentsBy(list),
                pageable,
                searchHist.getTotalHits()
        );
    }

    private List<Oppsummeringsdokument> getAllCurrentDocumentsBy(NativeSearchQueryBuilder builder) {
        builder.withSort(SortBuilders.fieldSort("lastModified").order(SortOrder.ASC));
        var list = operations.search(
                builder.build(),
                OppsummeringsdokumentModel.class
        ).get().map(SearchHit::getContent).collect(Collectors.toList());
        return getAllCurrentDocumentsBy(list);
    }

    public List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo) {
        return getAllCurrentDocumentsBy(new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.matchQuery("miljo", miljo)
                )
        );
    }

    public Page<Oppsummeringsdokument> getAllCurrentDocumentsBy(
            String miljo,
            LocalDate fom,
            LocalDate tom,
            String ident,
            String typeArbeidsforhold,
            Integer page
    ) {
        var pageable = PageRequest.of(page, 1);
        var queryBuilders = new ArrayList<QueryBuilder>();
        queryBuilders.add(QueryBuilders.matchQuery("miljo", miljo));
        getKalendermaanedBetween(fom, tom).ifPresent(queryBuilders::add);
        Optional.ofNullable(ident).ifPresent(value -> queryBuilders.add(
                QueryBuilders.matchQuery("virksomheter.personer.ident", value)
        ));
        Optional.ofNullable(typeArbeidsforhold).ifPresent(value -> queryBuilders.add(
                QueryBuilders.matchQuery("virksomheter.personer.arbeidsforhold.typeArbeidsforhold", value)
        ));
        return getAllCurrentDocumentsBy(
                new NativeSearchQueryBuilder()
                        .withQuery(combinedOnANDOperator(queryBuilders))
                        .withPageable(pageable),
                pageable
        );
    }


    private List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, LocalDate fom, LocalDate tom) {
        var queryBuilders = new ArrayList<QueryBuilder>();
        queryBuilders.add(QueryBuilders.matchQuery("miljo", miljo));
        getKalendermaanedBetween(fom, tom).ifPresent(queryBuilders::add);

        return getAllCurrentDocumentsBy(new NativeSearchQueryBuilder()
                .withQuery(combinedOnANDOperator(queryBuilders))
        );
    }


    private Optional<RangeQueryBuilder> getKalendermaanedBetween(LocalDate fom, LocalDate tom) {
        if (fom == null && tom == null) {
            return Optional.empty();
        }
        var builder = QueryBuilders.rangeQuery("kalendermaaned");

        if (fom != null) {
            builder.gte(fom.withDayOfMonth(1));
        }

        if (tom != null) {
            builder.lte(tom.withDayOfMonth(tom.lengthOfMonth()));
        }
        return Optional.of(builder);
    }

    private QueryBuilder combinedOnANDOperator(List<QueryBuilder> list){
        var queryBuilder = QueryBuilders.boolQuery();
        for (var item : list){
            queryBuilder.must(item);
        }
        return queryBuilder;
    }

    /**
     * TODO Find a way to do this operation by elastic search
     */
    private List<Oppsummeringsdokument> getAllCurrentDocumentsBy(List<OppsummeringsdokumentModel> list) {
        return list
                .stream()
                .collect(Collectors.groupingBy(s -> s.getKalendermaaned().withDayOfMonth(1) + s.getOpplysningspliktigOrganisajonsnummer()))
                .values()
                .stream()
                .map(items -> items.stream().reduce(null, (total, value) -> {
                    if (total == null || total.getVersion() < value.getVersion()) {
                        total = value;
                    }
                    return total;
                }))
                .map(Oppsummeringsdokument::new)
                .collect(Collectors.toList());
    }


    public Oppsummeringsdokument getCurrentDocumentBy(LocalDate kalendermaaned, String orgnummer, String miljo) {
        var list = getAllCurrentDocumentsBy(
                miljo,
                kalendermaaned.withDayOfMonth(1),
                kalendermaaned.withDayOfMonth(kalendermaaned.lengthOfMonth())
        );

        if (list.size() > 1) {
            log.warn(
                    "Fant flere en av samme versioner for kalendermaaned: {} og orgnummer: {}. Velger den første i listen.",
                    kalendermaaned,
                    orgnummer
            );
        }
        return list.stream().findFirst().orElse(null);
    }
}
