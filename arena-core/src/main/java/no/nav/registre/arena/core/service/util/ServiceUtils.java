package no.nav.registre.arena.core.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Adresse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Forvalter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.forvalter.Konto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.service.BrukereService;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.response.RelasjonsResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceUtils {

    public static final String BEGRUNNELSE = "Syntetisert rettighet";
    public static final int MIN_ALDER_AAP = 18;
    public static final int MAX_ALDER_AAP = 67;
    public static final int MIN_ALDER_UNG_UFOER = 18;
    public static final int MAX_ALDER_UNG_UFOER = 36;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final BrukereService brukereService;
    private final Random rand;

    public List<String> getLevende(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId);
    }

    public List<String> getUtvalgteIdenter(
            Long avspillergruppeId,
            int antallNyeIdenter
    ) {
        var levendeIdenter = hodejegerenConsumer.getLevende(avspillergruppeId);
        Collections.shuffle(levendeIdenter);
        return levendeIdenter.subList(0, antallNyeIdenter);
    }

    public List<String> getUtvalgteIdenterIAldersgruppe(
            Long avspillergruppeId,
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder
    ) {
        var levendeIdenterIAldersgruppe = hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder, maksimumAlder);
        Collections.shuffle(levendeIdenterIAldersgruppe);
        return levendeIdenterIAldersgruppe.subList(0, antallNyeIdenter);
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
        //TODO: Finn ut hvilke kvalifiseringsgrupper som er vanligst her
        return opprettArbeidssoeker(rettigheter, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM);
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
            if (!nyeBrukereResponse.getNyBrukerFeilList().isEmpty()) {
                nyeBrukereResponse.getNyBrukerFeilList().forEach(nyBrukerFeil -> {
                    log.error("Kunne ikke opprette ny bruker i arena: {}", nyBrukerFeil.getMelding());
                    feiledeIdenter.add(nyBrukerFeil.getPersonident());
                });
            }
            rettigheter.removeIf(rettighet -> feiledeIdenter.contains(rettighet.getPersonident()));
        }
        return rettigheter;
    }

    public List<String> getIdenterMedFoedselsmelding(Long avspillergruppeId) {
        return hodejegerenConsumer.getFoedte(avspillergruppeId, null, null);
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

    public AktivitetskodeMedSannsynlighet velgAktivitetBasertPaaSannsynlighet(List<AktivitetskodeMedSannsynlighet> aktivitetskoder) {
        int totalSum = 0;
        for (var a : aktivitetskoder) {
            totalSum += a.getSannsynlighet();
        }

        int index = rand.nextInt(totalSum);
        int sum = 0;
        int i = 0;
        while (sum < index) {
            sum += aktivitetskoder.get(i++).getSannsynlighet();
        }

        return aktivitetskoder.get(Math.max(0, i - 1));
    }
}
