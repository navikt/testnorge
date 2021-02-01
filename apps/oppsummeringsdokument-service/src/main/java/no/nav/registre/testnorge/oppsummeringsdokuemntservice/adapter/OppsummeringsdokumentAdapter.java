package no.nav.registre.testnorge.oppsummeringsdokuemntservice.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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

    private Page<Oppsummeringsdokument> getAllCurrentDocumentsBy(Query query, Pageable pageable) {

        var searchHist = operations.search(
                query,
                OppsummeringsdokumentModel.class
        );

        var list = searchHist.get().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(
                getAllCurrentDocumentsBy(list),
                pageable,
                searchHist.getTotalHits()
        );
    }

    private List<Oppsummeringsdokument> getAllCurrentDocumentsBy(Query query) {
        var list = operations.search(
                query,
                OppsummeringsdokumentModel.class
        ).get().map(SearchHit::getContent).collect(Collectors.toList());
        return getAllCurrentDocumentsBy(list);
    }

    public List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo) {
        return getAllCurrentDocumentsBy(new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.matchQuery("miljo", miljo)
                )
                .build()
        );
    }

    public Page<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, LocalDate fom, LocalDate tom, Integer page) {
        var pageable = PageRequest.of(page, 1);
        var builder = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.matchQuery("miljo", miljo)
                ).withPageable(pageable);
        getKalendermaanedBetween(fom, tom).ifPresent(builder::withQuery);
        return getAllCurrentDocumentsBy(builder.build(), pageable);
    }


    private List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, LocalDate fom, LocalDate tom) {
        var builder = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.matchQuery("miljo", miljo)
                );
        getKalendermaanedBetween(fom, tom).ifPresent(builder::withQuery);
        return getAllCurrentDocumentsBy(builder.build());
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
                .sorted((first, second) -> (int) (first.getLastModified().getEpochSecond() - second.getLastModified().getEpochSecond()))
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
