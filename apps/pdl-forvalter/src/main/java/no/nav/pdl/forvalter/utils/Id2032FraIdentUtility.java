package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@UtilityClass
public class Id2032FraIdentUtility {

    private static final int[] VEKTER_K1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final List<Integer> GYLDIG_REST_K1 = List.of(1, 2, 3);

    /**
     * Validerer et fødsels-eller-d-nummer(1964 og 2032-type) ved å sjekke kontrollsifrene iht.
     * <a href="https://skatteetaten.github.io/folkeregisteret-api-dokumentasjon/nytt-fodselsnummer-fra-2032/">...</a>
     *
     * Denne utility antar at input er et gyldig personidentifikasjonsnummer, og gjør ikke en full validering, men
     * kun validering av kontrollsiffer K1 for å bestemme om det er en 2032-type eller ikke.
     *
     * @param ident 11-siffret FNR, DNR eller NPID som skal valideres.
     * @return true hvis gyldig, ellers false
     */
    public static boolean isId2032(String ident) {

        final int[] sifre = konverterTilIntArray(ident);
        final int gittK1 = sifre[9];

        final int[] grunnlagK1 = Arrays.copyOfRange(sifre, 0, VEKTER_K1.length);
        final int vektetK1 = IntStream.range(0, VEKTER_K1.length)
                .map(i -> grunnlagK1[i] * VEKTER_K1[i])
                .sum();

        final int beregnetRestSifferK1 = (vektetK1 + gittK1) % 11;

        return GYLDIG_REST_K1.contains(beregnetRestSifferK1);
    }

    private static int[] konverterTilIntArray(String ident) {

        int[] ints = new int[ident.length()];

        for (int i = 0; i < ident.length(); i++) {
            ints[i] = Character.getNumericValue(ident.charAt(i));
        }
        return ints;
    }
}
