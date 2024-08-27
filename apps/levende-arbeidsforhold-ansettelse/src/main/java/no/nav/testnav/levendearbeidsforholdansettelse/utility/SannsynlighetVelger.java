package no.nav.testnav.levendearbeidsforholdansettelse.utility;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.DatoIntervall;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Utility for å initialere alderspennlisten for sannsynlighetsfordelingen
 */
@Slf4j
@UtilityClass
public class SannsynlighetVelger {

        /* Tallene er funnet fra: https://www.ssb.no/arbeid-og-lonn/sysselsetting/statistikk/antall-arbeidsforhold-og-lonn
         i tabell 1 */

    private static final Integer antallAlder18to24 = 434106;
    private static final Integer antallAlder25to39 = 1022448;
    private static final Integer antallAlder40to54 = 976833;
    private static final Integer antallAlder55to66 = 563804;
    private static final Integer antallAlder67to72 = 72363;

    private static final List<Integer> ALDERSDISTRIBUSJON = List.of(antallAlder18to24, antallAlder25to39,
            antallAlder40to54, antallAlder55to66, antallAlder67to72);
    private static final Integer SUMMERT = ALDERSDISTRIBUSJON.stream()
            .mapToInt(Integer::intValue)
            .sum();

    private static final Random RANDOM = new SecureRandom();
    private static final Double propabilityAlder18to24 = (double) antallAlder18to24 / SUMMERT;
    private static final Double propabilityAlder25to39 = (double) antallAlder25to39 / SUMMERT;
    private static final Double propabilityAlder40to54 = (double) antallAlder40to54 / SUMMERT;
    private static final Double propabilityAlder55to66 = (double) antallAlder55to66 / SUMMERT;
    private static final Double propabilityAlder67to72 = (double) antallAlder67to72 / SUMMERT;

    private static final List<List<Integer>> alderListe = List.of(
            List.of(18, 24),
            List.of(25, 39),
            List.of(40, 54),
            List.of(55, 66),
            List.of(67, 72)
    );

    public static List<DatoIntervall> getDatointervaller() {

        return alderListe.stream()
                .map(aldersspenn -> DatoIntervall.builder()
                        .tom(LocalDate.now().minusYears(aldersspenn.getFirst()))
                        .fom(LocalDate.now().minusYears(aldersspenn.getLast()))
                        .build())
                .toList();
    }

    private static int getRandomIntervall() {

        var random = RANDOM.nextDouble();
        if (random < propabilityAlder18to24) {
            return 0;
        } else if (random < propabilityAlder18to24 + propabilityAlder25to39) {
            return 1;
        } else if (random < propabilityAlder18to24 + propabilityAlder25to39 + propabilityAlder40to54) {
            return 2;
        } else if (random < propabilityAlder18to24 + propabilityAlder25to39 + propabilityAlder40to54 + propabilityAlder55to66) {
            return 3;
        } else {
            return 4;
        }
    }

    public static Map<Integer, Integer> getFordeling(int antall) {

        var fordeling = new HashMap<Integer, Integer>();

        for (var i = 0; i < antall; i++) {

            var intervall = getRandomIntervall();
            if (fordeling.containsKey(intervall)) {
                fordeling.put(intervall, fordeling.get(intervall) + 1);
            } else {
                fordeling.put(intervall, 1);
            }
        }

        log.info("Stokastisk fordeling {}", fordeling);
        return fordeling;
    }
}
