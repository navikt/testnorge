package no.nav.dolly.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.dto.SearchRequest;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static no.nav.dolly.elastic.utils.PersonQueryUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchSearchService {

    private final BestillingElasticRepository bestillingElasticRepository;
    private final RandomSearchHelperService randomSearchHelperService;

    public List<ElasticBestilling> getAll() {

        var all = bestillingElasticRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .toList();
    }

    public List<String> getTyper(ElasticTyper[] typer) {

        var criteria = new Criteria();

        Arrays.stream(typer)
                .map(this::getCriteria)
                .forEach(criteria::and);

        return randomSearchHelperService.search(criteria);
    }

    public List<String> search(SearchRequest request) {

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
                    addHarDeltBostedQuery(criteria, request);
                    addHarKontaktinformasjonForDoedsboQuery(criteria, request);
                    addHarUtenlandskIdentifikasjonsnummerQuery(criteria, request);
                    addHarFalskIdentitetQuery(criteria, request);
                    addHarTilrettelagtKommunikasjonQuery(criteria, request);
                    addHarSikkerhetstiltakQuery(criteria, request);
                    addStatsborgerskapQuery(criteria, request);
                    addHarOppholdQuery(criteria, request);
                });

        return randomSearchHelperService.search(criteria);
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