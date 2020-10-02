package no.nav.registre.arena.core.service.util;

import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.EIER;
import static no.nav.registre.arena.core.service.util.IdentUtils.hentFoedseldato;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.TiltakArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFinnTiltakRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeFinnTiltakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import no.nav.registre.arena.core.consumer.rs.AktoerRegisteretConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.service.BrukereService;
import no.nav.registre.arena.core.service.exception.ArbeidssoekerException;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.response.Relasjon;
import no.nav.registre.testnorge.consumers.hodejegeren.response.RelasjonsResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.response.internal.DataRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.response.internal.HistorikkRequest;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Adresse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Forvalter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Konto;

@Slf4j
@Service
@RequiredArgsConstructor
@DependencyOn("testnorge-hodejegeren")
public class ServiceUtils {

    public static final String BEGRUNNELSE = "Syntetisert rettighet";
    public static final String DELTAKERSTATUS_GJENNOMFOERES = "GJENN";
    public static final int MIN_ALDER_AAP = 18;
    public static final int MAX_ALDER_AAP = 67;
    public static final int MIN_ALDER_UNG_UFOER = 18;
    public static final int MAX_ALDER_UNG_UFOER = 36;
    private static final String KILDE_ARENA = "arena";
    private static final int PAGE_SIZE = 10;
    private static final String RELASJON_BARN = "BARN";

    public static final String AKTIVITETSFASE_SYKEPENGEERSTATNING = "SPE";

    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsats;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final BrukereService brukereService;
    private final AktoerRegisteretConsumer aktoerRegisteretConsumer;
    private final Random rand;
    private final TiltakArenaForvalterConsumer tiltakArenaForvalterConsumer;

    static {
        aktivitestsfaserMedInnsats = new HashMap<>();
        URL resourceInnsatser = Resources.getResource("aktfase_til_innsats.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, List<KodeMedSannsynlighet>> map = objectMapper.readValue(resourceInnsatser, new TypeReference<>() {
            });
            aktivitestsfaserMedInnsats.putAll(map);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn innsatskoder.", e);
        }
    }


    public List<String> getLevende(
            Long avspillergruppeId,
            String miljoe
    ) {
        var levendeIdenterAsSet = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId));
        return filtrerEksisterendeBrukereIArena(levendeIdenterAsSet, miljoe);
    }

    public List<String> getUtvalgteIdenter(
            Long avspillergruppeId,
            int antallNyeIdenter,
            String miljoe
    ) {
        var levendeIdenter = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId));
        return filtrerIdenterUtenAktoerId(levendeIdenter, miljoe, antallNyeIdenter);
    }

    public List<String> getUtvalgteIdenterIAldersgruppe(
            Long avspillergruppeId,
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            String miljoe
    ) {
        var levendeIdenterIAldersgruppe = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder, maksimumAlder));
        return filtrerIdenterUtenAktoerId(levendeIdenterIAldersgruppe, miljoe, antallNyeIdenter);
    }

    public List<String> getUtvalgteIdenterIAldersgruppeMedBarnUnder18(
            Long avspillergruppeId,
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            String miljoe,
            LocalDate tidligsteDato
    ) {
        var levendeIdenterIAldersgruppe = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder, maksimumAlder));
        return filtrerIdenterUtenAktoerIdOgBarnUnder18(levendeIdenterIAldersgruppe, miljoe, antallNyeIdenter, tidligsteDato);
    }

    public List<KontoinfoResponse> getIdenterMedKontoinformasjon(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        if (antallNyeIdenter == 0) {
            return new ArrayList<>();
        }
        var identerMedKontonummer = hodejegerenConsumer.getIdenterMedKontonummer(avspillergruppeId, miljoe, antallNyeIdenter, null, null);
        Collections.shuffle(identerMedKontonummer);
        return identerMedKontonummer;
    }

    public List<RettighetRequest> opprettArbeidssoekerAap(
            List<RettighetRequest> rettigheter,
            String miljoe
    ) {
        return opprettArbeidssoeker(rettigheter, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.VARIG);
    }

    public List<RettighetRequest> opprettArbeidssoekerAap(
            List<RettighetRequest> rettigheter,
            String miljoe,
            String aktivitetsfase
    ) {
        if (aktivitetsfase == null || aktivitetsfase.isBlank()) {
            return opprettArbeidssoeker(rettigheter, miljoe, Kvalifiseringsgrupper.BATT);
        } else {
            return opprettArbeidssoeker(rettigheter, miljoe, velgKvalifiseringsgruppeBasertPaaAktivitetsfase(aktivitetsfase));
        }
    }

    public List<RettighetRequest> opprettArbeidssoekerTiltak(
            List<RettighetRequest> rettigheter,
            String miljoe
    ) {
        return opprettArbeidssoeker(rettigheter, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM);
    }

    public List<RettighetRequest> opprettArbeidssoekerTillegg(
            List<RettighetRequest> rettigheter,
            String miljoe
    ) {
        return opprettArbeidssoeker(rettigheter, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM);
    }

    private Kvalifiseringsgrupper velgKvalifiseringsgruppeBasertPaaAktivitetsfase(String aktivitetsfase) {
        if (aktivitestsfaserMedInnsats.containsKey(aktivitetsfase)){
            var innsats = velgKodeBasertPaaSannsynlighet(aktivitestsfaserMedInnsats.get(aktivitetsfase)).getKode();
            return Kvalifiseringsgrupper.valueOf(innsats);
        }else{
            throw new ArbeidssoekerException("Ukjent aktivitetsfase " + aktivitetsfase);
        }
    }

    private List<String> filtrerIdenterUtenAktoerId(
            Set<String> identer,
            String miljoe,
            int antallNyeIdenter
    ) {
        var identerUtenArenabruker = filtrerEksisterendeBrukereIArena(identer, miljoe);
        var identerPartisjonert = partisjonerListe(identerUtenArenabruker, PAGE_SIZE);
        Map<String, String> identerMedAktoerId = new HashMap<>();
        for (var partisjon : identerPartisjonert) {
            identerMedAktoerId.putAll(aktoerRegisteretConsumer.hentAktoerIderTilIdenter(partisjon, miljoe));
            if (identerMedAktoerId.size() >= antallNyeIdenter) {
                break;
            }
        }
        return new ArrayList<>(identerMedAktoerId.keySet()).subList(0, antallNyeIdenter);
    }

    private List<String> filtrerIdenterUtenAktoerIdOgBarnUnder18(
            Set<String> identer,
            String miljoe,
            int antallNyeIdenter,
            LocalDate tidligsteDato
    ) {
        var identerUtenArenabruker = filtrerEksisterendeBrukereIArena(identer, miljoe);

        var identerPartisjonert = partisjonerListe(identerUtenArenabruker, PAGE_SIZE);

        List<String> utvalgteIdenter = new ArrayList<>(antallNyeIdenter);

        for (var partisjon : identerPartisjonert) {
            Map<String, String> identerMedAktoerId = aktoerRegisteretConsumer.hentAktoerIderTilIdenter(partisjon, miljoe);

            for (var ident : identerMedAktoerId.keySet()) {
                var relasjonsResponse = getRelasjonerTilIdent(ident, miljoe);

                for (var relasjon : relasjonsResponse.getRelasjoner()) {
                    if (erRelasjonEtBarnUnder18VedTidspunkt(relasjon, tidligsteDato)) {
                        utvalgteIdenter.add(ident);
                        if (utvalgteIdenter.size() >= antallNyeIdenter) {
                            return utvalgteIdenter;
                        }
                    }
                }
            }
        }
        return utvalgteIdenter;
    }

    private boolean erRelasjonEtBarnUnder18VedTidspunkt(
            Relasjon relasjon,
            LocalDate tidspunkt
    ) {
        if (RELASJON_BARN.equals(relasjon.getTypeRelasjon())) {
            var doedsdato = relasjon.getDatoDo();
            if (doedsdato != null && !doedsdato.equals("")) {
                return false;
            }

            var barnFnr = relasjon.getFnrRelasjon();

            int alder = Math.toIntExact(ChronoUnit.YEARS.between(hentFoedseldato(barnFnr), tidspunkt));

            return alder > -1 && alder < 18;
        }
        return false;
    }

    private List<String> filtrerEksisterendeBrukereIArena(
            Set<String> identerAsSet,
            String miljoe
    ) {
        var eksisterendeBrukere = new HashSet<>(brukereService.hentEksisterendeArbeidsoekerIdenter(EIER, miljoe));
        identerAsSet.removeAll(eksisterendeBrukere);
        var identer = new ArrayList<>(identerAsSet);
        Collections.shuffle(identer);
        return identer;
    }

    private List<RettighetRequest> opprettArbeidssoeker(
            List<RettighetRequest> rettigheter,
            String miljoe,
            Kvalifiseringsgrupper kvalifiseringsgruppe
    ) {
        var identerIArena = brukereService.hentEksisterendeArbeidsoekerIdenter();
        var uregistrerteBrukere = rettigheter.stream().filter(rettighet -> !identerIArena.contains(rettighet.getPersonident())).map(RettighetRequest::getPersonident)
                .collect(Collectors.toSet());

        if (!uregistrerteBrukere.isEmpty()) {
            var nyeBrukereResponse = brukereService
                    .sendArbeidssoekereTilArenaForvalter(new ArrayList<>(uregistrerteBrukere), miljoe, kvalifiseringsgruppe);
            List<String> feiledeIdenter = new ArrayList<>();
            if (nyeBrukereResponse != null && nyeBrukereResponse.getNyBrukerFeilList() != null && !nyeBrukereResponse.getNyBrukerFeilList().isEmpty()) {
                nyeBrukereResponse.getNyBrukerFeilList().forEach(nyBrukerFeil -> {
                    log.error("Kunne ikke opprette ny bruker med fnr {} i arena: {}", nyBrukerFeil.getPersonident(), nyBrukerFeil.getMelding());
                    feiledeIdenter.add(nyBrukerFeil.getPersonident());
                });
            }
            rettigheter.removeIf(rettighet -> feiledeIdenter.contains(rettighet.getPersonident()));
        }
        return rettigheter;
    }

    public List<String> getIdenterMedFoedselsmelding(
            Long avspillergruppeId,
            int maxAlder
    ) {
        return hodejegerenConsumer.getFoedte(avspillergruppeId, null, maxAlder);
    }

    public RelasjonsResponse getRelasjonerTilIdent(
            String ident,
            String miljoe
    ) {
        return hodejegerenConsumer.getRelasjoner(ident, miljoe);
    }

    public static Forvalter buildForvalter(KontoinfoResponse identMedKontoinfo) {
        Konto konto = Konto.builder()
                .kontonr(identMedKontoinfo.getKontonummer())
                .build();
        Adresse adresse = Adresse.builder()
                .adresseLinje1(identMedKontoinfo.getAdresseLinje1())
                .adresseLinje2(identMedKontoinfo.getAdresseLinje2())
                .adresseLinje3(identMedKontoinfo.getAdresseLinje3())
                .fodselsnr(identMedKontoinfo.getFnr())
                .landkode(identMedKontoinfo.getLandkode())
                .navn(identMedKontoinfo.getLandkode())
                .postnr(identMedKontoinfo.getPostnr())
                .build();
        return Forvalter.builder()
                .gjeldendeKontonr(konto)
                .utbetalingsadresse(adresse)
                .build();
    }

    public KodeMedSannsynlighet velgKodeBasertPaaSannsynlighet(List<KodeMedSannsynlighet> koder) {
        int totalSum = 0;
        for (var a : koder) {
            totalSum += a.getSannsynlighet();
        }

        int index = rand.nextInt(totalSum);
        int sum = 0;
        int i = 0;
        while (sum < index) {
            sum += koder.get(i++).getSannsynlighet();
        }

        return koder.get(Math.max(0, i - 1));
    }

    public void lagreIHodejegeren(Map<String, List<NyttVedtakResponse>> identerMedOpprettedeRettigheter) {
        List<DataRequest> identMedData = new ArrayList<>();
        for (var identMedRettigheter : identerMedOpprettedeRettigheter.entrySet()) {
            var rettigheterSomObject = new ArrayList<>();
            for (var nyttVedtakResponse : identMedRettigheter.getValue()) {
                var nyeRettigheterAap = nyttVedtakResponse.getNyeRettigheterAap();
                if (nyeRettigheterAap != null) {
                    rettigheterSomObject.addAll(nyeRettigheterAap);
                }
            }
            if (!rettigheterSomObject.isEmpty()) {
                var dataRequest = new DataRequest();
                dataRequest.setId(identMedRettigheter.getKey());
                dataRequest.setData(rettigheterSomObject);
                identMedData.add(dataRequest);
            } else {
                log.warn("Kunne ikke opprette historikk i hodejegeren på ident {}", identMedRettigheter.getKey());
            }
        }
        if (!identMedData.isEmpty()) {
            var historikkRequest = new HistorikkRequest();
            historikkRequest.setKilde(KILDE_ARENA);
            historikkRequest.setIdentMedData(identMedData);
            hodejegerenConsumer.saveHistory(historikkRequest);
        }
    }

    private <T> Collection<List<T>> partisjonerListe(
            List<T> list,
            long partitionSize
    ) {
        AtomicInteger counter = new AtomicInteger(0);
        return list
                .stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / partitionSize))
                .values();
    }

    public void setDatoPeriodeVedtakInnenforMaxAntallMaaneder(
            NyttVedtak vedtak,
            int antallMaaneder
    ) {
        var tilDato = vedtak.getTilDato();
        if (tilDato != null) {
            var tilDatoLimit = vedtak.getFraDato().plusMonths(antallMaaneder);

            if (tilDato.isAfter(tilDatoLimit)) {
                vedtak.setTilDato(tilDatoLimit);
            }
        }
    }

    public boolean harNoedvendigTiltaksdeltakelse(NyttVedtakTiltak vedtak, List<NyttVedtakTiltak> tiltaksdeltakelser) {
        if (tiltaksdeltakelser != null && !tiltaksdeltakelser.isEmpty()) {
            var fraDato = vedtak.getFraDato();
            var tilDato = vedtak.getTilDato();
            for (var deltakelse : tiltaksdeltakelser) {
                var fraDatoDeltakelse = deltakelse.getFraDato();
                var tilDatoDeltakelse = deltakelse.getTilDato();
                if (fraDatoDeltakelse != null && tilDatoDeltakelse != null &&
                        fraDato.isAfter(fraDatoDeltakelse.minusDays(1)) &&
                        tilDato.isBefore(tilDatoDeltakelse.plusDays(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<NyttVedtakTiltak> finnTiltak(String personident, String miljoe, List<NyttVedtakTiltak> tiltaksdeltakelser){
        List<NyttVedtakTiltak> tiltak = new ArrayList<>();
        List<Integer> tiltaksIder = new ArrayList<>();
        if (!tiltaksdeltakelser.isEmpty()){
            var rettighetRequest = new RettighetFinnTiltakRequest(tiltaksdeltakelser);

            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);

            var responses = tiltakArenaForvalterConsumer.finnTiltak(rettighetRequest);

            tiltak = finnGyldigeTiltakUtenDuplikater(responses);
        }
        return tiltak;
    }

    private List<NyttVedtakTiltak> finnGyldigeTiltakUtenDuplikater(List<NyeFinnTiltakResponse> responses){
        List<NyttVedtakTiltak> tiltak = new ArrayList<>();
        List<Integer> tiltaksIder = new ArrayList<>();

        for (var response: responses){
            if (response != null && response.getNyeFinntiltakFeilList().isEmpty()) {
                for (var finntiltak : response.getNyeFinnTiltak()){
                    var tiltakId = finntiltak.getTiltakId();
                    if (tiltakId != null && !tiltaksIder.contains(tiltakId)) {
                        tiltaksIder.add(tiltakId);
                        tiltak.add(finntiltak);
                    }
                }
            }
        }

        return tiltak;
    }
}
