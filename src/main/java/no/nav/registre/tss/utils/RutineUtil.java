package no.nav.registre.tss.utils;

public class RutineUtil {

    private static final int LENGDE_PAA_910_MELDING = 63;

    public static String opprett910Rutine() {
        StringBuilder stringBuilder = new StringBuilder(LENGDE_PAA_910_MELDING);
        for (int i = 0; i < LENGDE_PAA_910_MELDING; i++) {
            stringBuilder.replace(i, i + 1, " ");
        }
        return stringBuilder.toString();
    }

    public static String setIdKodeI910Rutine(StringBuilder rutine, String idKode) {
        int start = 0;
        checkLength(idKode, 3);
        int length = idKode.length();
        return rutine.replace(start, start + length, idKode).toString();
    }

    public static String setIdTssEksternI910Rutine(StringBuilder rutine, String idTssEkstern) {
        int start = 3;
        checkLength(idTssEkstern, 11);
        int length = idTssEkstern.length();
        return rutine.replace(start, start + length, idTssEkstern).toString();
    }

    public static String setIdOffI910Rutine(StringBuilder rutine, String idOff) {
        int start = 14;
        checkLength(idOff, 11);
        int length = idOff.length();
        return rutine.replace(start, start + length, idOff).toString();
    }

    public static String setKodeIdenttypeI910Rutine(StringBuilder rutine, String kodeIdenttype) {
        int start = 25;
        checkLength(kodeIdenttype, 4);
        int length = kodeIdenttype.length();
        return rutine.replace(start, start + length, kodeIdenttype).toString();
    }

    public static String setKodeSamhTypeI910Rutine(StringBuilder rutine, String kodeSamh) {
        int start = 29;
        checkLength(kodeSamh, 4);
        int length = kodeSamh.length();
        return rutine.replace(start, start + length, kodeSamh).toString();
    }

    public static String setAvdelingnrI910Rutine(StringBuilder rutine, String avdelingnr) {
        int start = 33;
        checkLength(avdelingnr, 2);
        int length = avdelingnr.length();
        return rutine.replace(start, start + length, avdelingnr).toString();
    }

    public static String setAvdOffnrI910Rutine(StringBuilder rutine, String avdOffnr) {
        int start = 35;
        checkLength(avdOffnr, 11);
        int length = avdOffnr.length();
        return rutine.replace(start, start + length, avdOffnr).toString();
    }

    public static String setDatoI910Rutine(StringBuilder rutine, String dato) {
        int start = 46;
        checkLength(dato, 8);
        int length = dato.length();
        return rutine.replace(start, start + length, dato).toString();
    }

    public static String setHistorikkI910Rutine(StringBuilder rutine, String historikk) {
        int start = 54;
        checkLength(historikk, 1);
        int length = historikk.length();
        return rutine.replace(start, start + length, historikk).toString();
    }

    public static String setBrukeridI910Rutine(StringBuilder rutine, String brukerid) {
        int start = 55;
        checkLength(brukerid, 8);
        int length = brukerid.length();
        return rutine.replace(start, start + length, brukerid).toString();
    }

    private static void checkLength(String field, int maxLength) {
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Kan ikke være lenger enn " + maxLength);
        }
    }
}
