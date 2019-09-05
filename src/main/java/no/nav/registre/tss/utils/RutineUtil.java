package no.nav.registre.tss.utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RutineUtil {

    public static final int TOTAL_LENGTH = 203;

    public static String padTilLengde(String rutine) {
        StringBuilder finalRutine = new StringBuilder(rutine);
        String padding = " ";

        String extraPadding = IntStream.range(rutine.length(), TOTAL_LENGTH).mapToObj(i -> padding).collect(Collectors.joining(""));

        finalRutine.append(extraPadding);

        return finalRutine.toString();
    }

}
