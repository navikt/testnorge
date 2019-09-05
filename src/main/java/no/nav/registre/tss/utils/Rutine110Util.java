package no.nav.registre.tss.utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Rutine110Util {

    private static final String FORMAT = "COB";
    private static final String KILDE = "SYNT";
    private static final String BRUKERID = "ORK     ";
    private static final String KJOERENR = "         ";


    private static final int MMEL_LENGTH = 228;

    public static String leggTilHeader(String rutine) {
        StringBuilder fullRutine = new StringBuilder(rutine);
        fullRutine.insert(0, opprettHeader());
        return fullRutine.toString();
    }

    public static String opprettHeader() {
        StringBuilder header = new StringBuilder(FORMAT);
        String mmelSymbol = " ";
        String mmel = IntStream.range(0, MMEL_LENGTH).mapToObj(i -> mmelSymbol).collect(Collectors.joining(""));
        header.append(mmel).append(KILDE).append(BRUKERID).append(KJOERENR);
        return header.toString();
    }
}
