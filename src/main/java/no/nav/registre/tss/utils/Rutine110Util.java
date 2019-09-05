package no.nav.registre.tss.utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Rutine110Util {

    private static final String FORMAT = "COB";
    private static final String KILDE = "SYNT";
    private static final String BRUKERID = "ORK     ";
    private static final String KJOERENR = "000000000";

    private static final int MMEL_LENGTH = 228;

    public static String leggTilHeader(String rutine) {
        StringBuilder fullRutine = new StringBuilder(rutine);
        fullRutine.insert(0, opprettHeader());
        return fullRutine.toString();
    }

    // midl. til synt blir fikset
    /*
    fyll inn J på oppdater
    samh-type var 1 for langt til høyre
    navn 3 for langt til høyre
     */
    public static String fiksPosisjoner(String rutine) {
        StringBuilder stringBuilder = new StringBuilder(rutine);
        stringBuilder.replace(18, 19, "");
        stringBuilder.replace(23, 25, "");
        stringBuilder.insert(100, " ");
        stringBuilder.insert(178, "  ");
        return stringBuilder.toString();
    }

    public static String setOppdater(String rutine) {
        return new StringBuilder(rutine).replace(178, 179, "J").toString();
    }

    private static String opprettHeader() {
        StringBuilder header = new StringBuilder(FORMAT);
        String mmelSymbol = " ";
        String mmel = IntStream.range(0, MMEL_LENGTH).mapToObj(i -> mmelSymbol).collect(Collectors.joining(""));
        header.append(mmel).append(KILDE).append(BRUKERID).append(KJOERENR);
        return header.toString();
    }
}
