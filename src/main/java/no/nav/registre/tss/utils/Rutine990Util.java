package no.nav.registre.tss.utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Rutine990Util {

    private static final String FORMAT = "COB";
    private static final int MMEL_LENGTH = 228;
    private static final int LENGDE_PAA_990_MELDING = 15;

    public static String opprettRutine(String kodetabell, String brukerId) {
        StringBuilder rutine = new StringBuilder(LENGDE_PAA_990_MELDING);
        for (int i = 0; i < LENGDE_PAA_990_MELDING; i++) {
            rutine.replace(i, i + 1, " ");
        }

        setIdKode(rutine, "990");
        setKodetabell(rutine, kodetabell);
        setBrukerId(rutine, brukerId);

        leggTilHeader(rutine);
        return rutine.toString();
    }

    private static void leggTilHeader(StringBuilder rutine) {
        rutine.insert(0, opprettHeader());
    }

    private static String opprettHeader() {
        StringBuilder header = new StringBuilder(FORMAT);
        String mmelSymbol = " ";
        String mmel = IntStream.range(0, MMEL_LENGTH).mapToObj(i -> mmelSymbol).collect(Collectors.joining(""));
        header.append(mmel);
        return header.toString();
    }

    public static void setIdKode(StringBuilder rutine, String idKode) {
        int start = 0;
        checkLength(idKode, 3);
        int length = idKode.length();
        rutine.replace(start, start + length, idKode);
    }

    private static void setKodetabell(StringBuilder rutine, String kodetabell) {
        int start = 3;
        checkLength(kodetabell, 4);
        int length = kodetabell.length();
        rutine.replace(start, start + length, kodetabell);
    }

    private static void setBrukerId(StringBuilder rutine, String brukerId) {
        int start = 7;
        checkLength(brukerId, 8);
        int length = brukerId.length();
        rutine.replace(start, start + length, brukerId);
    }

    private static void checkLength(String field, int maxLength) {
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Felt " + field + " er for langt. Kan ikke være lenger enn " + maxLength);
        }
    }
}
