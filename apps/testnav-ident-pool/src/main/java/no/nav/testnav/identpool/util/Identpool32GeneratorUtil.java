package no.nav.testnav.identpool.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

@Slf4j
@UtilityClass
public class Identpool32GeneratorUtil {

    private static final int[] WEIGHTS_K1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] WEIGHTS_K2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    public static List<String> generateIdents(String datoIdentifikator, int individnummer) {

        return genrerateIdents(datoIdentifikator, individnummer, 3);
    }

    private static List<String> genrerateIdents(String datoIdentifikator, int individnummer, int antallForsoek) {

        var ident = datoIdentifikator +
                String.format("%03d", individnummer--);

        var identer = Arrays.stream(calculateK1(ident))
                .filter(k1 -> k1 != 10)
                .map(k1 -> ident + String.format("%1d", k1))
                .map(identMedK1 -> {
                        var k2 = calculateK2(identMedK1);
                        return k2 != 10 ?
                        identMedK1 + String.format("%1d", k2) : null;
                })
                .filter(Objects::nonNull)
                .toList();

        if (antallForsoek > 0 && identer.isEmpty()) {
            log.info("Feil ved generering av ident med datoIdentifikator {} og individnummer {}, prøver med individnummer {}",
                    datoIdentifikator, individnummer + 1, individnummer);
            return genrerateIdents(datoIdentifikator, individnummer, --antallForsoek);
        }
        return identer;
    }

    private static Integer[] calculateK1(String ident) {

        var weightedK1 = IntStream.range(0, WEIGHTS_K1.length)
                .map(i -> parseInt(valueOf(ident.charAt(i))) * WEIGHTS_K1[i])
                .sum();

        int remainder = weightedK1 % 11;
        return new Integer[]{
                alternateK1(remainder, 14),
                alternateK1(remainder, 13),
                alternateK1(remainder, 12),
                alternateK1(remainder, 11)};
    }

    private static int alternateK1(int reminder, int controlDigit) {

        return (controlDigit - reminder) % 11;
    }

    private static int calculateK2(String ident) {

        var weightedK2 = IntStream.range(0, WEIGHTS_K2.length)
                .map(i -> parseInt(valueOf(ident.charAt(i))) * WEIGHTS_K2[i])
                .sum();

        int remainder = 11 - weightedK2 % 11;

        return remainder % 11;
    }
}
