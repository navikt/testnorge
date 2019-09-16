package no.nav.registre.tss.utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Rutine960Util {

    private static final String FORMAT = "COB";
    private static final int MMEL_LENGTH = 228;
    private static final int LENGDE_PAA_960_MELDING = 63;

    public static String opprettRutine(String ident) {
        StringBuilder rutine = new StringBuilder(LENGDE_PAA_960_MELDING);
        for (int i = 0; i < LENGDE_PAA_960_MELDING; i++) {
            rutine.replace(i, i + 1, " ");
        }

        setIdKode(rutine, "960");
        setIdOff(rutine, ident);
        setKodeIdenttype(rutine, "FNR");

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

    public static void setIdTssEkstern(StringBuilder rutine, String idTssEkstern) {
        int start = 3;
        checkLength(idTssEkstern, 11);
        int length = idTssEkstern.length();
        rutine.replace(start, start + length, idTssEkstern);
    }

    public static void setIdOff(StringBuilder rutine, String idOff) {
        int start = 14;
        checkLength(idOff, 11);
        int length = idOff.length();
        rutine.replace(start, start + length, idOff);
    }

    public static void setKodeIdenttype(StringBuilder rutine, String kodeIdenttype) {
        int start = 25;
        checkLength(kodeIdenttype, 4);
        int length = kodeIdenttype.length();
        rutine.replace(start, start + length, kodeIdenttype);
    }

    public static void setKodeSamhType(StringBuilder rutine, String kodeSamh) {
        int start = 29;
        checkLength(kodeSamh, 4);
        int length = kodeSamh.length();
        rutine.replace(start, start + length, kodeSamh);
    }

    public static void setAvdelingnr(StringBuilder rutine, String avdelingnr) {
        int start = 33;
        checkLength(avdelingnr, 2);
        int length = avdelingnr.length();
        rutine.replace(start, start + length, avdelingnr);
    }

    public static void setAvdOffnr(StringBuilder rutine, String avdOffnr) {
        int start = 35;
        checkLength(avdOffnr, 11);
        int length = avdOffnr.length();
        rutine.replace(start, start + length, avdOffnr);
    }

    public static void setDato(StringBuilder rutine, String dato) {
        int start = 46;
        checkLength(dato, 8);
        int length = dato.length();
        rutine.replace(start, start + length, dato);
    }

    public static void setHistorikk(StringBuilder rutine, String historikk) {
        int start = 54;
        checkLength(historikk, 1);
        int length = historikk.length();
        rutine.replace(start, start + length, historikk);
    }

    public static void setBrukerid(StringBuilder rutine, String brukerid) {
        int start = 55;
        checkLength(brukerid, 8);
        int length = brukerid.length();
        rutine.replace(start, start + length, brukerid);
    }

    private static void checkLength(String field, int maxLength) {
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Kan ikke være lenger enn " + maxLength);
        }
    }
}
