package no.nav.testnav.identpool.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.dto.NoekkelinfoDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

@Slf4j
@UtilityClass
public class Identpool32GeneratorUtil {

    private static final int[] WEIGHTS_K1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] WEIGHTS_K2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    public static List<String> generateIdents(NoekkelinfoDTO noekkelinfo) {

        var ident = noekkelinfo.datoIdentifikator() +
                String.format("%03d", noekkelinfo.individnummer());

        return Arrays.stream(calculateK1(ident))
                .filter(k1 -> k1 != 10)
                .map(k1 -> ident + String.format("%1d", k1))
                .map(identMedK1 -> identMedK1 + String.format("%1d", calculateK2(identMedK1)))
                .filter(Identpool32GeneratorUtil::isValidLenth)
                .toList();
    }

    private static Integer[] calculateK1(String ident) {

        var weightedK1 = IntStream.range(0, WEIGHTS_K1.length)
                .map(i -> parseInt(valueOf(ident.charAt(i))) * WEIGHTS_K1[i])
                .sum();

        int remainder = weightedK1 % 11;
        return new Integer[]{
                alternateK1(remainder, 14),
                alternateK1(remainder, 13),
                alternateK1(remainder, 12)};
        // controlDigit = 11 benyttes ikke for ikke å gi overlapp med eksisterende løsning
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

    private static boolean isValidLenth(String ident) {

        return ident.length() == 11;
    }
}
