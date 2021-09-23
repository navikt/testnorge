package no.nav.registre.skd.service;

import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.putFnrInnIMelding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.requests.HentIdenterRequest;
import no.nav.registre.skd.consumer.requests.HentIdenterRequest.IdentType;
import no.nav.registre.skd.exceptions.ManglerEksisterendeIdentException;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;
import no.nav.testnav.libs.servletcore.util.IdentUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoedselService {

    private static final int MIN_FORELDER_ALDER = 16;
    private static final int MAKS_FORELDER_ALDER = 70;

    private final IdentPoolConsumer identPoolConsumer;

    private final Random rand;

    public List<String> behandleFoedselsmeldinger(
            IdentType identType,
            List<RsMeldingstype> meldinger,
            List<String> levendeIdenterINorge
    ) {
        if (levendeIdenterINorge.isEmpty()) {
            throw new ManglerEksisterendeIdentException("Kunne ikke finne mor til ident for SkdMelding med meldingsnummer "
                    + meldinger.get(0).getMeldingsnrHosTpsSynt() + ". For få identer i listen levendeIdenterINorge.");
        }

        var moedre = findMoedre(meldinger.size(), levendeIdenterINorge, meldinger.get(0).getMeldingsnrHosTpsSynt());
        List<String> barn = new ArrayList<>(meldinger.size());

        var meldingIterator = meldinger.iterator();
        int i = 0;
        while (meldingIterator.hasNext()) {
            var melding = meldingIterator.next();

            var morFnr = moedre.get(i++);
            var morFoedselsdato = IdentUtil.getFoedselsdatoFraIdent(morFnr);

            String barnFnr;
            try {
                var foedtFoer = morFoedselsdato.plusYears(MAKS_FORELDER_ALDER);
                if (foedtFoer.isAfter(LocalDate.now())) {
                    foedtFoer = LocalDate.now();
                }
                barnFnr = identPoolConsumer.hentNyeIdenter(
                        HentIdenterRequest.builder()
                                .foedtEtter(morFoedselsdato.plusYears(MIN_FORELDER_ALDER))
                                .foedtFoer(foedtFoer)
                                .antall(1)
                                .identtype(identType)
                                .rekvirertAv("orkestratoren")
                                .build())
                        .get(0);
            } catch (RuntimeException e) {
                log.warn("Kunne ikke finne barn til mor med fnr {} - Fjernet melding med meldingsnummer {}", morFnr, melding.getMeldingsnrHosTpsSynt());
                meldingIterator.remove();
                continue;
            }

            putFnrInnIMelding((RsMeldingstype1Felter) melding, barnFnr);
            ((RsMeldingstype1Felter) melding).setRegDato(IdentUtil.getFoedselsdatoFraIdent(barnFnr).toString().replaceAll("-", ""));

            ((RsMeldingstype1Felter) melding).setMorsFodselsdato(morFnr.substring(0, 6));
            ((RsMeldingstype1Felter) melding).setMorsPersonnummer(morFnr.substring(6));

            var shouldFindFar = rand.nextBoolean(); // noen får farskapsmelding senere
            if (shouldFindFar) {
                var farFnr = findFar(morFnr, barnFnr, levendeIdenterINorge, moedre);
                if (farFnr != null) {
                    ((RsMeldingstype1Felter) melding).setFarsFodselsdato(farFnr.substring(0, 6));
                    ((RsMeldingstype1Felter) melding).setFarsPersonnummer(farFnr.substring(6));
                } else {
                    log.info("Kunne ikke finne far til barn med fnr {}. morFnr: {}", barnFnr, morFnr);
                }
            }

            barn.add(barnFnr);
        }

        return barn;
    }

    public List<String> findMoedre(
            int antallNyeIdenter,
            List<String> levendeIdenterINorge,
            String meldingsnrHosTpsSynt
    ) {
        List<String> moedre = new ArrayList<>(antallNyeIdenter);
        List<String> potensielleMoedre = new ArrayList<>(levendeIdenterINorge);

        potensielleMoedre.removeIf(potensiellMor -> !"02468".contains(String.valueOf(potensiellMor.charAt(8)))); // kvinner har partall på index 8 i FNR

        if (potensielleMoedre.isEmpty()) {
            throw new ManglerEksisterendeIdentException("Kunne ikke finne mor til ident for SkdMelding med meldingsnummer "
                    + meldingsnrHosTpsSynt + ". For få kvinner i listen levendeIdenterINorge.");
        }

        for (int i = 0; i < antallNyeIdenter; i++) {
            int randomIndex = rand.nextInt(potensielleMoedre.size());
            moedre.add(potensielleMoedre.get(randomIndex));
        }

        return moedre;
    }

    /**
     * @param morsFnr
     * @param barnFnr
     * @param levendeIdenterINorge
     * @param moedre
     * @return Fnr til far til barn. Far kan være yngre, like gammel eller eldre enn mor, og minst MIN_FORELDER_ALDER år eldre enn barnet. Far vil alltid være mann
     */
    public String findFar(
            String morsFnr,
            String barnFnr,
            List<String> levendeIdenterINorge,
            List<String> moedre
    ) {
        List<String> potensielleFedre = new ArrayList<>(levendeIdenterINorge);
        potensielleFedre.removeIf(potensiellFar -> !"13579".contains(String.valueOf(potensiellFar.charAt(8)))); // menn har oddetall på index 8 i FNR
        Collections.shuffle(potensielleFedre);

        var foedselsdatoTilMor = IdentUtil.getFoedselsdatoFraIdent(morsFnr);
        var foedselsdatoTilBarn = IdentUtil.getFoedselsdatoFraIdent(barnFnr);
        var dagensDato = LocalDate.now();

        var morAlder = dagensDato.minusYears(foedselsdatoTilMor.getYear()).getYear();
        var barnAlder = dagensDato.minusYears(foedselsdatoTilBarn.getYear()).getYear();
        var gyldigMinimumFarAlder = morAlder - 30;
        var gyldigMaksimumFarAlder = morAlder + 30;

        if (gyldigMinimumFarAlder < barnAlder + MIN_FORELDER_ALDER) {
            gyldigMinimumFarAlder = barnAlder + MIN_FORELDER_ALDER;
        }

        if (gyldigMaksimumFarAlder > barnAlder + MAKS_FORELDER_ALDER) {
            gyldigMaksimumFarAlder = barnAlder + MAKS_FORELDER_ALDER;
        }

        for (var far : potensielleFedre) {
            if (!moedre.contains(far)) {
                var foedselsdatoTilFar = IdentUtil.getFoedselsdatoFraIdent(far);
                var farAlder = dagensDato.minusYears(foedselsdatoTilFar.getYear()).getYear();
                if (farAlder >= gyldigMinimumFarAlder && farAlder <= gyldigMaksimumFarAlder) {
                    return far;
                }
            }
        }
        return null;
    }
}
