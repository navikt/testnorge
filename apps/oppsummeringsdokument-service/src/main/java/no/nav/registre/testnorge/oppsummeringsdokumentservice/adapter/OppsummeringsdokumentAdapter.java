package no.nav.registre.testnorge.oppsummeringsdokumentservice.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.consumer.AaregSyntConsumer;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.domain.Oppsummeringsdokument;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.OppsummeringsdokumentRepository;
import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.OppsummeringsdokumentModel;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OppsummeringsdokumentAdapter {

    private final ObjectMapper objectMapper;
    private final OppsummeringsdokumentRepository repository;
    private final ElasticsearchOperations operations;
    private final AaregSyntConsumer aaregSyntConsumer;

    public void deleteAllBy(String miljo, Populasjon populasjon) {
        repository.deleteAllByMiljoAndPopulasjon(miljo, populasjon);
    }

    public void deleteAllBy(String miljo) {
        repository.deleteAllByMiljo(miljo);
    }

    @SneakyThrows
    public String save(Oppsummeringsdokument oppsummeringsdokument, String miljo, String origin) {
        log.info("Oppretter oppsummeringsdokument for opplysningspliktig {} i {}...",
                oppsummeringsdokument.getOpplysningspliktigOrganisajonsnummer(), miljo);
        aaregSyntConsumer.saveOpplysningspliktig(oppsummeringsdokument, miljo);

        try {
            var id = repository.save(oppsummeringsdokument.toModel(miljo, origin)).getId();
            log.info("Oppsummeringsdokument (id: {}) opprett for opplysningsplikitg {} i {}.", id, oppsummeringsdokument.getOpplysningspliktigOrganisajonsnummer(), miljo);
            return id;
        } catch (UncategorizedElasticsearchException ex) {
            log.error("Feil ved innsending av \n{}", objectMapper.writeValueAsString(oppsummeringsdokument.toDTO()), ex);
            throw ex;
        }

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
                filterOnVersion(list),
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
        return filterOnVersion(list);
    }

    public List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo) {
        return getAllCurrentDocumentsBy(new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.matchQuery("miljo", miljo)
                )
        );
    }

    public Page<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, Integer page) {
        var pageable = PageRequest.of(page, 1);
        return getAllCurrentDocumentsBy(new NativeSearchQueryBuilder()
                        .withQuery(
                                QueryBuilders.matchQuery("miljo", miljo)
                        ).withPageable(pageable),
                pageable
        );
    }


    public List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, String ident) {
        var queryBuilders = new ArrayList<QueryBuilder>();
        queryBuilders.add(QueryBuilders.matchQuery("miljo", miljo));

        queryBuilders.add(QueryBuilders.matchQuery("virksomheter.personer.ident", ident));

        return getAllCurrentDocumentsBy(new NativeSearchQueryBuilder().withQuery(combinedOnANDOperator(queryBuilders)));
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


    private List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, String orgnummer, LocalDate fom, LocalDate tom) {
        var queryBuilders = new ArrayList<QueryBuilder>();

        queryBuilders.add(QueryBuilders.matchQuery("miljo", miljo));
        queryBuilders.add(QueryBuilders.matchQuery("opplysningspliktigOrganisajonsnummer", orgnummer));

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

    private QueryBuilder combinedOnANDOperator(List<QueryBuilder> list) {
        var queryBuilder = QueryBuilders.boolQuery();
        for (var item : list) {
            queryBuilder.must(item);
        }
        return queryBuilder;
    }

    /**
     * TODO Find a way to do this operation by elastic search
     */
    private List<Oppsummeringsdokument> filterOnVersion(List<OppsummeringsdokumentModel> list) {
        return list
                .stream()
                .collect(Collectors.groupingBy(item -> item.getKalendermaaned().withDayOfMonth(1) + item.getOpplysningspliktigOrganisajonsnummer()))
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
                orgnummer,
                kalendermaaned.withDayOfMonth(1),
                kalendermaaned.withDayOfMonth(kalendermaaned.lengthOfMonth())
        );

        if (list.size() > 1) {
            log.warn(
                    "Fant flere med samme versjon for kalendermaaned: {} og orgnummer: {}. Velger den først i listen.",
                    kalendermaaned,
                    orgnummer
            );
        }
        return list.stream().findFirst().orElse(null);
    }
}
