package no.nav.dolly.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.dto.SearchRequest;
import no.nav.dolly.elastic.dto.SearchResponse;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

import static no.nav.dolly.elastic.utils.PersonQueryUtils.addAdressebeskyttelseQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addBarnQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addBostedBydelsnrQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addBostedKommuneQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addBostedMatrikkelQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addBostedPostnrQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addBostedUkjentQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addBostedUtlandQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addDoedsfallQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addForeldreQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addFullmaktQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarDeltBostedQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarDoedfoedtbarnQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarFalskIdentitetQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarForeldreansvarQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarInnflyttingQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarKontaktadresseQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarKontaktinformasjonForDoedsboQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarNyIdentitetQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarOppholdQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarOppholdsadresseQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarSikkerhetstiltakQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarTilrettelagtKommunikasjonQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarUtenlandskIdentifikasjonsnummerQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addHarUtflyttingQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addSivilstandQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addStatsborgerskapQuery;
import static no.nav.dolly.elastic.utils.PersonQueryUtils.addVergemaalQuery;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchSearchService {

    private final SearchHelperService searchHelperService;

    public SearchHits<ElasticBestilling> getAll(SearchRequest request) {

        return searchHelperService.getRaw(getCriteria(request));
    }
    public SearchHits<ElasticBestilling> getAll(String ident) {

        var identCriteria = new Criteria("identer").contains(ident);
        return searchHelperService.getRaw(identCriteria);
    }

    public SearchResponse getTyper(ElasticTyper[] typer) {

        var criteria = new Criteria();

        Arrays.stream(typer)
                .map(this::getCriteria)
                .forEach(criteria::and);

        try {
            return searchHelperService.search(criteria);

        } catch (
                UncategorizedElasticsearchException | ConverterNotFoundException e) {

            log.warn("Feilet å utføre søk {}: {}", criteria, e.getLocalizedMessage());
            return SearchResponse.builder()
                    .error(e.getLocalizedMessage())
                    .build();
        }
    }

    public SearchResponse search(SearchRequest request) {

        var criteria = getCriteria(request);

        try {
            return searchHelperService.search(criteria);

        } catch (
                UncategorizedElasticsearchException | ConverterNotFoundException e) {

            log.warn("Feilet å utføre søk {}: {}", criteria, e.getLocalizedMessage());
            return SearchResponse.builder()
                    .error(e.getLocalizedMessage())
                    .build();
        }
    }

    private Criteria getCriteria(SearchRequest request) {

        var criteria = new Criteria();

        request.getTyper().stream()
                .map(this::getCriteria)
                .forEach(criteria::and);

        Optional.ofNullable(request.getPersonRequest())
                .ifPresent(value -> {
                    addBarnQuery(criteria, request);
                    addForeldreQuery(criteria, request);
                    addSivilstandQuery(criteria, request);
                    addHarDoedfoedtbarnQuery(criteria, request);
                    addHarForeldreansvarQuery(criteria, request);
                    addVergemaalQuery(criteria, request);
                    addFullmaktQuery(criteria, request);
                    addDoedsfallQuery(criteria, request);
                    addHarInnflyttingQuery(criteria, request);
                    addHarUtflyttingQuery(criteria, request);
                    addAdressebeskyttelseQuery(criteria, request);
                    addHarOppholdsadresseQuery(criteria, request);
                    addHarKontaktadresseQuery(criteria, request);
                    addBostedKommuneQuery(criteria, request);
                    addBostedPostnrQuery(criteria, request);
                    addBostedBydelsnrQuery(criteria, request);
                    addBostedUtlandQuery(criteria, request);
                    addBostedMatrikkelQuery(criteria, request);
                    addBostedUkjentQuery(criteria, request);
                    addHarDeltBostedQuery(criteria, request);
                    addHarKontaktinformasjonForDoedsboQuery(criteria, request);
                    addHarUtenlandskIdentifikasjonsnummerQuery(criteria, request);
                    addHarFalskIdentitetQuery(criteria, request);
                    addHarTilrettelagtKommunikasjonQuery(criteria, request);
                    addHarSikkerhetstiltakQuery(criteria, request);
                    addStatsborgerskapQuery(criteria, request);
                    addHarOppholdQuery(criteria, request);
                    addHarNyIdentitetQuery(criteria, request);
                });
        return criteria;
    }

    private Criteria getCriteria(ElasticTyper type) {

        return switch (type) {
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
            case BANKKONTO -> new Criteria("bankkonto").exists();
            case ARBEIDSPLASSENCV -> new Criteria("arbeidsplassenCV").exists();
        };
    }
}