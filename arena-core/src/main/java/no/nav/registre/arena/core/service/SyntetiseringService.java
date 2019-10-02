package no.nav.registre.arena.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.AAPNyRettighetSyntetisererenConsumer;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.AAPMelding;
import no.nav.registre.arena.domain.Aap;
import no.nav.registre.arena.domain.Arbeidsoeker;
import no.nav.registre.arena.core.provider.rs.requests.IdentMedData;
import no.nav.registre.arena.domain.NyBruker;
import no.nav.registre.arena.domain.aap.andreokonomytelser.AndreOkonomYtelserV1;
import no.nav.registre.arena.domain.aap.andreokonomytelser.AnnenOkonomYtelseV1;
import no.nav.registre.arena.domain.aap.andreokonomytelser.OkonomKoder;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.GensakKoder;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.Saksopplysning;
import no.nav.registre.arena.domain.aap.institusjonsopphold.Institusjonsopphold;
import no.nav.registre.arena.domain.aap.vilkaar.Vilkaar;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.floor;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final String ARENA_FORVALTER_NAME = "arena-forvalteren";
    private static final double PROSENTANDEL_SOM_SKAL_HA_MELDEKORT = 0.2;
    private int MINIMUM_ALDER = 16;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final AAPNyRettighetSyntetisererenConsumer aapConsumer;
    private final Random random;


    public List<Arbeidsoeker> opprettArbeidsoekere(Integer antallNyeIdenter, Long avspillergruppeId, String miljoe) {
        List<String> levendeIdenter = hentLevendeIdenter(avspillergruppeId);
        log.info("Fant {} eksisternende identer i Hodejegeren.", levendeIdenter.size());
        List<String> arbeidsoekerIdenter = hentEksisterendeArbeidsoekerIdenter();
        log.info("Fant {} eksisternende identer i Arena Forvalteren", arbeidsoekerIdenter.size());

        if (antallNyeIdenter == null) {
            int antallArbeidsoekereAaOpprette = getAntallBrukereForAaFylleArenaForvalteren(levendeIdenter.size(), arbeidsoekerIdenter.size());

            if (antallArbeidsoekereAaOpprette > 0) {
                antallNyeIdenter = antallArbeidsoekereAaOpprette;
            } else {
                log.info("{}% av gyldige brukere funnet av hodejegeren er allerede registrert i Arena.",
                        (PROSENTANDEL_SOM_SKAL_HA_MELDEKORT * 100));
                return new ArrayList<>();
            }
            log.info("Oppretter arbeidsoekere for {} identer for aa faa {}% tilgjengelige identer i arena forvalteren.",
                    antallArbeidsoekereAaOpprette, (PROSENTANDEL_SOM_SKAL_HA_MELDEKORT * 100));
        }

        List<String> nyeIdenter = hentKvalifiserteIdenter(antallNyeIdenter, levendeIdenter, arbeidsoekerIdenter);
        return byggArbeidsoekereOgLagreIHodejegeren(nyeIdenter, miljoe);
    }

    public List<Arbeidsoeker> opprettArbeidssoeker(String ident, Long avspillergruppeId, String miljoe) {
        List<String> levendeIdenter = hentLevendeIdenter(avspillergruppeId);
        log.info("Fant {} ledige identer i hodejegeren.", levendeIdenter.size());
        List<String> arbeidsoekerIdenter = hentEksisterendeArbeidsoekerIdenter();
        log.info("Fant {} eksisterende identer i Arena Forvalteren.", arbeidsoekerIdenter.size());

        if (arbeidsoekerIdenter.contains(ident)) {
            log.info("Ident {} er allerede registrert som arbeidsøker.", ident);
            return arenaForvalterConsumer.hentArbeidsoekere(ident, null, null);
        } else if (!levendeIdenter.contains(ident)) {
            log.info("Ident {} kunne ikke bli funnet av Hodejegeren, og kan derfor ikke opprettes i Arena.", ident);
            return new ArrayList<>();
        }

        return byggArbeidsoekereOgLagreIHodejegeren(Collections.singletonList(ident), miljoe);
    }

    private List<String> hentKvalifiserteIdenter(int antallIdenter, List<String> levendeIdenter, List<String> eksisterendeArbeidsoekere) {
        levendeIdenter = levendeIdenter.parallelStream()
                .filter(eksisterendeArbeidsoekere::contains)
                .collect(Collectors.toList());

        if (levendeIdenter.size() <= 0) {
            log.info("Alle identer som ble funnet i hodejegeren eksisterer allerede i Arena Forvalter.");
            return new ArrayList<>();
        }

        if (antallIdenter > levendeIdenter.size()) {
            antallIdenter = levendeIdenter.size();
            log.info("Fant ikke nok ledige identer i avspillergruppe. Lager meldekort på {} nye identer.", antallIdenter);
        }

        List<String> nyeIdenter = new ArrayList<>(antallIdenter);

        for (int i = 0; i < antallIdenter; i++) {
            nyeIdenter.add(levendeIdenter.remove(random.nextInt(levendeIdenter.size())));
        }


        return nyeIdenter;
    }

    private List<String> hentLevendeIdenter(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER);
    }

    private List<Arbeidsoeker> byggArbeidsoekereOgLagreIHodejegeren(List<String> identer, String miljoe) {
        List<AAPMelding> rettigheter = aapConsumer.hentAAPMeldingerFraSyntRest(identer.size());

        List<NyBruker> nyeBrukere = identer.stream().map(ident -> NyBruker.builder()
                .personident(ident)
                .miljoe(miljoe)
                .kvalifiseringsgruppe("IKVAL")
                .automatiskInnsendingAvMeldekort(true)
                .aap(Collections.singletonList(new Aap()))
                //.aap(Collections.singletonList(byggAAP(rettigheter.remove(0))))
                .build()).collect(Collectors.toList());
        lagreArenaBrukereIHodejegeren(nyeBrukere);

        return arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
    }

    private boolean validString(String s) {
            return !Objects.isNull(s) && s.trim().isEmpty();
    }

    private void addOkonomiskYtelse(OkonomKoder kode, String verdi, List<AnnenOkonomYtelseV1> liste) {
        if (validString(verdi)) {
            liste.add(new AnnenOkonomYtelseV1(kode, verdi));
        }
    }

    private void addGenerelleSaksopplysninger(GensakKoder kode, String verdi, List<Saksopplysning> liste) {
        if (validString(verdi)) {
            liste.add();
        }
    }

    private Aap byggAAP(AAPMelding syntMelding) {

        List<AnnenOkonomYtelseV1> okonomYtelse = new ArrayList<>();
        List<Saksopplysning> saksopplysninger = new ArrayList<>();
        List<Institusjonsopphold> institusjonsopphold = new ArrayList<>();
        List<Vilkaar> vilkaar = new ArrayList<>();

        // OKONOMISKE YTELSER
        addOkonomiskYtelse(OkonomKoder.TYPE, syntMelding.getTYPE(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.FDATO, syntMelding.getFDATO(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.TDATO, syntMelding.getTDATO(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.GRAD, syntMelding.getGRAD(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.BELOP, syntMelding.getBELOP(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.BELPR, syntMelding.getBELPR(), okonomYtelse);
        AndreOkonomYtelserV1 okonomYtelserLister = new AndreOkonomYtelserV1(okonomYtelse);

        // SAKSOPPLYSNINGER
        if (!Objects.isNull(syntMelding.getKDATO())) {
            saksopplysninger.add(new Saksopplysning(GensakKoder.KDATO, syntMelding.getKDATO()));
        }
        if (!Objects.isNull(syntMelding.getBTID())) {
            saksopplysninger.add(new Saksopplysning(GensakKoder.BTID, syntMelding.getBTID()));
        }
        if (!Objects.isNull(syntMelding.getTUUIN())) {
            saksopplysninger.add(new Saksopplysning(GensakKoder.TUUIN, syntMelding.getTUUIN()));
        }
        if (!Objects.isNull(syntMelding.getUUFOR())) {
            saksopplysninger.add(new Saksopplysning(GensakKoder.UUFOR, syntMelding.getUUFOR()));
        }
        if (!Objects.isNull(syntMelding.getSTUBE())) {
            saksopplysninger.add(new Saksopplysning(GensakKoder.STUBE, syntMelding.getSTUBE()));
        }
        if (!Objects.isNull(syntMelding.getOTILF())) {
            saksopplysninger.add(new Saksopplysning(GensakKoder.OTILF, syntMelding.getOTILF()));
        }
        if (!Objects.isNull(syntMelding.getOTSEK())) {
            saksopplysninger.add(new Saksopplysning(GensakKoder.OTSEK, syntMelding.getOTSEK()));
        }
        if (!Objects.isNull(syntMelding.getOOPPL())) {
            saksopplysninger.add(new Saksopplysning(GensakKoder.OOPPL, syntMelding.getOOPPL()));
        }

        Aap melding = Aap.builder()
                .andreOkonomYtelser(Collections.singletonList(okonomYtelserLister))
                .fraDato(syntMelding.getFRA_DATO())
                .tilDato(syntMelding.getTIL_DATO())
                .utfall(syntMelding.getUTFALL())
                .aktivitetsfase(syntMelding.getAKTFASEKODE())
                .vedtaksvariant(syntMelding.getVEDTAKSVARIANT())
                .build();
    }

    private void lagreArenaBrukereIHodejegeren(List<NyBruker> nyeBrukere) {

        List<IdentMedData> brukereSomSkalLagres = new ArrayList<>();

        for (NyBruker bruker : nyeBrukere) {

            List<NyBruker> data = Collections.singletonList(bruker);
            brukereSomSkalLagres.add(new IdentMedData(bruker.getPersonident(), data));

        }
        hodejegerenConsumer.saveHistory(ARENA_FORVALTER_NAME, brukereSomSkalLagres);
    }

    private List<String> hentEksisterendeArbeidsoekerIdenter() {
        List<Arbeidsoeker> arbeidsoekere = arenaForvalterConsumer.hentArbeidsoekere(null, null, null);
        return hentIdentListe(arbeidsoekere);
    }

    private List<String> hentIdentListe(List<Arbeidsoeker> arbeidsoekere) {

        if (arbeidsoekere.isEmpty()) {
            log.info("Fant ingen eksisterende identer.");
            return new ArrayList<>();
        }

        return arbeidsoekere.stream().map(Arbeidsoeker::getPersonident).collect(Collectors.toList());
    }

    private int getAntallBrukereForAaFylleArenaForvalteren(int antallLevendeIdenter, int antallEksisterendeIdenter) {
        return (int) (floor(antallLevendeIdenter * PROSENTANDEL_SOM_SKAL_HA_MELDEKORT) - antallEksisterendeIdenter);
    }
}
