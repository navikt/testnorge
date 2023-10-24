package no.nav.testnav.apps.oppsummeringsdokumentservice.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.oppsummeringsdokumentservice.consumer.AaregSyntConsumer;
import no.nav.testnav.apps.oppsummeringsdokumentservice.domain.Oppsummeringsdokument;
import no.nav.testnav.apps.oppsummeringsdokumentservice.repository.OppsummeringsdokumentRepository;
import no.nav.testnav.apps.oppsummeringsdokumentservice.repository.model.OppsummeringsdokumentModel;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class OppsummeringsdokumentAdapter {

    private static final String MILJO = "miljo";
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

    public List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo) {
        return getAllCurrentDocumentsBy(
                new CriteriaQueryBuilder(new Criteria(MILJO).is(miljo)
                ));
    }

    public Page<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, Integer page) {
        var pageable = PageRequest.of(page, 1);
        return getAllCurrentDocumentsBy(
                new CriteriaQueryBuilder(new Criteria(MILJO).is(miljo))
                        .withPageable(pageable),
                pageable
        );
    }

    public List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, String ident) {
        return getAllCurrentDocumentsBy(new CriteriaQueryBuilder(new Criteria(MILJO).is(miljo).and("virksomheter.personer.ident").is(ident))
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

        var criteria = new Criteria(MILJO).is(miljo);
        criteria = isNull(fom) ? criteria : criteria.and("kalendermaaned").greaterThanEqual(fom.withDayOfMonth(1));
        criteria = isNull(tom) ? criteria : criteria.and("kalendermaaned").lessThanEqual(tom.withDayOfMonth(tom.lengthOfMonth()));
        criteria = isNull(ident) ? criteria : criteria.and("virksomheter.personer.ident").is(ident);
        criteria = isNull(typeArbeidsforhold) ? criteria : criteria.and("virksomheter.personer.arbeidsforhold.typeArbeidsforhold").is(typeArbeidsforhold);

        return getAllCurrentDocumentsBy(
                new CriteriaQueryBuilder(criteria)
                        .withPageable(pageable),
                pageable
        );
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

    private Page<Oppsummeringsdokument> getAllCurrentDocumentsBy(CriteriaQueryBuilder builder, Pageable pageable) {

        builder.withSort(Sort.by("lastModified").ascending());
        var searchHist = operations.search(
                builder.build(),
                OppsummeringsdokumentModel.class
        );

        var list = searchHist.get().map(SearchHit::getContent)
                .toList();
        return new PageImpl<>(
                filterOnVersion(list),
                pageable,
                searchHist.getTotalHits()
        );
    }

    private List<Oppsummeringsdokument> getAllCurrentDocumentsBy(CriteriaQueryBuilder builder) {
        builder.withSort(Sort.by("lastModified").ascending());
        var list = operations.search(
                        builder.build(),
                        OppsummeringsdokumentModel.class
                ).get().map(SearchHit::getContent)
                .toList();
        return filterOnVersion(list);
    }

    private List<Oppsummeringsdokument> getAllCurrentDocumentsBy(String miljo, String orgnummer, LocalDate fom, LocalDate tom) {

        var criteria = new Criteria(MILJO).is(miljo).and("opplysningspliktigOrganisajonsnummer").is(orgnummer);
        criteria = isNull(fom) ? criteria : criteria.and("kalendermaaned").greaterThanEqual(fom.withDayOfMonth(1));
        criteria = isNull(tom) ? criteria : criteria.and("kalendermaaned").lessThanEqual(tom.withDayOfMonth(tom.lengthOfMonth()));

        return getAllCurrentDocumentsBy(new CriteriaQueryBuilder(criteria));
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
                })).filter(Objects::nonNull)
                .map(Oppsummeringsdokument::new)
                .toList();
    }
}