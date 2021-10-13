package no.nav.registre.medl.service;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import no.nav.registre.medl.domain.IdentMedData;
import no.nav.registre.medl.domain.MedlSaveInHodejegerenRequest;
import no.nav.registre.medl.domain.TMedlemPeriodeBase;
import no.nav.registre.medl.adapter.AktoerAdapter;
import no.nav.registre.medl.consumer.rs.AktoerRegisteretConsumer;
import no.nav.registre.medl.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.medl.consumer.rs.MedlSyntConsumer;
import no.nav.registre.medl.consumer.rs.response.MedlSyntResponse;
import no.nav.registre.medl.database.model.TAktoer;
import no.nav.registre.medl.database.model.TMedlemPeriode;
import no.nav.registre.medl.database.model.TStudieinformasjon;
import no.nav.registre.medl.database.repository.MedlemPeriodeRepository;
import no.nav.registre.medl.database.repository.StudieinformasjonRepository;
import no.nav.registre.medl.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final String MEDL_NAME = "medl";
    private static final int PAGE_SIZE = 1000;

    private final MedlSyntConsumer medlSyntRestConsumer;

    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    private final AktoerRegisteretConsumer aktoerRegisteretConsumer;

    private final MedlemPeriodeRepository medlemPeriodeRepository;

    private final StudieinformasjonRepository studieinformasjonRepository;

    private final HodejegerenConsumer hodejegerenConsumer;

    private final AktoerAdapter aktoerAdapter;

    public List<TMedlemPeriode> opprettMeldinger(SyntetiserMedlRequest syntetiserMedlRequest) {
        if (syntetiserMedlRequest.getProsentfaktor() < 0 || syntetiserMedlRequest.getProsentfaktor() > 1) {
            throw new IllegalArgumentException("Ugyldig prosentfaktor. Oppgi et tall mellom 0 og 1");
        }

        var levendeIdenter = finnLevendeIdenter(syntetiserMedlRequest.getAvspillergruppeId());
        int antallIdenter = (int) (levendeIdenter.size() * syntetiserMedlRequest.getProsentfaktor());

        var foersteTMedlemPeriodePage = medlemPeriodeRepository.findAll(PageRequest.of(0, PAGE_SIZE));

        List<Long> aktoerIder = foersteTMedlemPeriodePage
                .getContent()
                .stream()
                .map(TMedlemPeriode::getAktoerId)
                .collect(Collectors.toList());

        for (int i = 1; i < foersteTMedlemPeriodePage.getTotalPages(); i++) {
            aktoerIder.addAll(
                    medlemPeriodeRepository.findAll(PageRequest.of(i, PAGE_SIZE))
                            .getContent()
                            .stream()
                            .map(TMedlemPeriode::getAktoerId).collect(Collectors.toList())
            );
        }

        List<String> identerMedMedlemskap = aktoerAdapter.filtererAktoerIder(aktoerIder);
        identerMedMedlemskap.retainAll(levendeIdenter);

        int antallNyeIdenter = antallIdenter - identerMedMedlemskap.size();

        if (antallNyeIdenter <= 0) {
            log.info("Tilstrekkelig mange identer har medlemskap. Produserer ikke flere medlemskap.");
            return new ArrayList<>();
        }

        levendeIdenter.removeAll(identerMedMedlemskap);

        if (levendeIdenter.isEmpty() || levendeIdenter.size() < antallNyeIdenter) {
            log.info("Ikke mange nok ledige identer å opprette medlemskap på. Produserer ikke flere medlemskap.");
            return new ArrayList<>();
        }

        log.info("Oppretter {} nye medlemskap", antallNyeIdenter);

        Collections.shuffle(levendeIdenter);

        var nyeIdenter = levendeIdenter.subList(0, antallNyeIdenter);

        var medlSyntResponses = medlSyntRestConsumer.hentMedlemskapsmeldingerFromSyntRest(nyeIdenter.size());

        Map<String, String> fnrToAktoerer = partitionList(nyeIdenter, PAGE_SIZE)
                .stream()
                .map(list -> aktoerRegisteretConsumer.lookupAktoerIdFromFnr(list, syntetiserMedlRequest.getMiljoe()))
                .reduce(new HashMap<>(), (result, partitionResult) -> {
                    result.putAll(partitionResult);
                    return result;
                });

        var opprettedeMedlemskap = lagreMedl(medlSyntResponses, fnrToAktoerer);

        log.info("{} medlemskap ble opprettet", opprettedeMedlemskap.size());

        return opprettedeMedlemskap;
    }

    public TMedlemPeriode opprettDelvisMelding(
            MedlSyntResponse data,
            String fnr,
            String miljoe
    ) {
        var fnrToAktoer = aktoerRegisteretConsumer.lookupAktoerIdFromFnr(Collections.singletonList(fnr), miljoe);
        if (fnrToAktoer.isEmpty()) {
            log.error("Fant ingen identer med aktørid");
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Fnr finnes ikke hos aktør");
        }
        var medlSyntResponses = medlSyntRestConsumer.hentMedlemskapsmeldingerFromSyntRest(1);
        if (medlSyntResponses.isEmpty()) {
            log.error("Fikk ikke hentet syntetiske melding fra synt-medl");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Ingen meldinger hentet fra synt");
        }
        data.oppdaterMeldingFraAnnenMelding(medlSyntResponses.get(0));

        return lagreMedl(Collections.singletonList(data), fnrToAktoer).get(0);
    }

    @Timed(value = "medl.resource.latency", extraTags = {"operation", "hodejegeren"})
    private List<String> finnLevendeIdenter(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId);
    }

    private List<TMedlemPeriode> lagreMedl(
            List<MedlSyntResponse> data,
            Map<String, String> fnrToAktoer
    ) {
        var dataIter = data.listIterator();

        List<TMedlemPeriode> medlemskap = new ArrayList<>();

        List<String> fnrsForHistorikk = new ArrayList<>();

        for (var entry : fnrToAktoer.entrySet()) {
            var fnr = entry.getKey();
            var aktoerId = entry.getValue();

            if (dataIter.hasNext()) {
                var next = dataIter.next();
                var aktoer = aktoerAdapter.opprettAktoer(next, aktoerId, fnr);
                var studieinformasjon = opprettStudieInformasjon(next);
                medlemskap.add(opprettMedlemPeriode(next, aktoer, studieinformasjon));
                fnrsForHistorikk.add(fnr);
            } else {
                log.warn("Ikke nok syntetiske medlemskap for å opprette på alle identer");
                break;
            }
        }
        List<IdentMedData> identerMedData = new ArrayList<>(fnrsForHistorikk.size());
        int i = 0;
        for (var fnr : fnrsForHistorikk) {
            identerMedData.add(new IdentMedData(fnr,
                    Collections.singletonList(createTMedlemPeriodeBaseObject(medlemskap.get(i)))));
            i++;
        }
        var hodejegerenRequest = new MedlSaveInHodejegerenRequest(MEDL_NAME, identerMedData);

        var lagredeIdenter = hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequest);

        if (lagredeIdenter.size() < identerMedData.size()) {
            List<String> identerSomIkkeBleLagret = new ArrayList<>(identerMedData.size());
            for (var ident : identerMedData) {
                identerSomIkkeBleLagret.add(ident.getId());
            }
            identerSomIkkeBleLagret.removeAll(lagredeIdenter);
            log.warn("Kunne ikke lagre historikk på alle identer. Identer som ikke ble lagret: {}", identerSomIkkeBleLagret);
        }
        return medlemskap;
    }

    private TMedlemPeriodeBase createTMedlemPeriodeBaseObject(
            TMedlemPeriode tMedlemPeriode
    ) {
        return TMedlemPeriodeBase.builder()
                .periodeFom((tMedlemPeriode.getPeriodeFom() != null) ? tMedlemPeriode.getPeriodeFom().toLocalDate() : null)
                .periodeTom((tMedlemPeriode.getPeriodeTom() != null) ? tMedlemPeriode.getPeriodeTom().toLocalDate() : null)
                .type(tMedlemPeriode.getType())
                .status(tMedlemPeriode.getStatus())
                .dekning(tMedlemPeriode.getDekning())
                .lovvalg(tMedlemPeriode.getLovvalg())
                .statusaarsak(tMedlemPeriode.getStatusaarsak())
                .datoBrukFra((tMedlemPeriode.getDatoBrukFra() != null) ? tMedlemPeriode.getDatoBrukFra().toLocalDateTime() : null)
                .datoBrukTil((tMedlemPeriode.getDatoBrukTil() != null) ? tMedlemPeriode.getDatoBrukTil().toLocalDateTime() : null)
                .grunnlag(tMedlemPeriode.getGrunnlag())
                .land(tMedlemPeriode.getLand())
                .build();
    }

    private TMedlemPeriode opprettMedlemPeriode(
            MedlSyntResponse data,
            TAktoer aktoer,
            TStudieinformasjon studieinformasjon
    ) {
        TMedlemPeriode.TMedlemPeriodeBuilder tMedlemPeriodeBuilder =
                TMedlemPeriode.builder()
                        .aktoerId(aktoer.getId())
                        .datoBrukFra("".equals(data.getDatoBrukFra()) ? null : createTimestampFromDate(data.getDatoBrukFra()))
                        .datoEndret("".equals(data.getDatoEndret()) ? null : createTimestampFromDate(data.getDatoEndret()))
                        .datoIdentifisering("".equals(data.getDatoIdentifisering()) ? null : parseDateFormat(data.getDatoIdentifisering()))
                        .datoOpprettet("".equals(data.getDatoOpprettet()) ? null : createTimestampFromDate(data.getDatoOpprettet()))
                        .datoRegistrert("".equals(data.getDatoRegistrert()) ? null : parseDateFormat(data.getDatoRegistrert()))
                        .dekning(data.getDekning())
                        .endretAv(data.getEndretAv())
                        .grunnlag(data.getGrunnlag())
                        .kilde(data.getKilde())
                        .kildedokument(data.getKildedokument())
                        .land(data.getLand())
                        .lovvalg(data.getLovvalg())
                        .opprettetAv(data.getOpprettetAv())
                        .periodeFom("".equals(data.getPeriodeFom()) ? null : parseDateFormat(data.getPeriodeFom()))
                        .periodeTom("".equals(data.getPeriodeTom()) ? null : parseDateFormat(data.getPeriodeTom()))
                        .type(data.getType())
                        .status(data.getStatus())
                        .versjon(data.getVersjon())
                        .statusaarsak(data.getStatusaarsak())
                        .funkPeriodeId(medlemPeriodeRepository.nextFunctionalId())
                        .datoBeslutning("".equals(data.getDatoBeslutning()) ? null : parseDateFormat(data.getDatoBeslutning()))
                        .studieinformasjonId(studieinformasjon.getStudieinformasjonId())
                        .datoBrukTil("".equals(data.getDatoBrukTil()) ? null : createTimestampFromDate(data.getDatoBrukTil()));

        return medlemPeriodeRepository.save(tMedlemPeriodeBuilder.build());
    }

    private TStudieinformasjon opprettStudieInformasjon(
            MedlSyntResponse data
    ) {
        var studieinformasjonBuilder = TStudieinformasjon.builder();

        if (data.getInndatoLaanekassen() != null && !"".equals(data.getInndatoLaanekassen())) {
            studieinformasjonBuilder.delstudier(data.getDelstudier())
                    .godkjent(data.getGodkjent())
                    .inndatoLaanekassen(parseDateFormat(data.getInndatoLaanekassen()))
                    .statsborgerland(data.getStatsborgerland())
                    .studieland(data.getStudieland());
            return studieinformasjonRepository.save(studieinformasjonBuilder.build());
        }
        return null;
    }

    private Timestamp createTimestampFromDate(
            String partialDate
    ) {
        java.util.Date date = new java.util.Date();
        String strDateFormat = "hh:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        Date parseDateFormat = parseDateFormat(partialDate);
        return Timestamp.valueOf(parseDateFormat + " " + dateFormat.format(date));
    }

    private Date parseDateFormat(
            String date
    ) {
        String strDateFormat = "yyyy-dd-MM";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String normalFormat = "yyyy-MM-dd";
        DateFormat normalDateFormat = new SimpleDateFormat(normalFormat);
        try {
            java.util.Date s = dateFormat.parse(date);
            String s2 = normalDateFormat.format(s);
            return Date.valueOf(s2);
        } catch (ParseException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    private <T> Collection<List<T>> partitionList(List<T> list, long partitionSize) {
        AtomicInteger counter = new AtomicInteger(0);
        return list
                .stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / partitionSize))
                .values();
    }
}
