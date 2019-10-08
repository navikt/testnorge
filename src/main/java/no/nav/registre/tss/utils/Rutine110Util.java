package no.nav.registre.tss.utils;

import static no.nav.registre.tss.utils.RutineUtil.MELDINGSLENGDE;

import no.nav.registre.tss.consumer.rs.responses.TssMessage;

public class Rutine110Util {

    private static final String FORMAT = "COB";
    private static final String KILDE = "SYNT";
    private static final String BRUKERID = "ORK";
    private static final String KJOERENR = "000000000";
    private static final int LENGDE_PAA_110_HEADER = 251;

    public static String opprett110Rutine(TssMessage message) {
        StringBuilder rutine = new StringBuilder(MELDINGSLENGDE);
        for (int i = 0; i < MELDINGSLENGDE; i++) {
            rutine.replace(i, i + 1, " ");
        }

        setIdKode(rutine, message.getIdKode());
        setIdOff(rutine, message.getIdOff());
        setKodeIdenttype(rutine, message.getKodeIdenttype());
        setKodeSamhType(rutine, message.getKodeSamhType());
        setNavn(rutine, message.getNavn());
        setOppdater(rutine, message.getOppdater());

        leggTilHeader(rutine);
        return rutine.toString();
    }

    public static void leggTilHeader(StringBuilder rutine) {
        rutine.insert(0, opprettHeader());
    }

    private static String opprettHeader() {
        StringBuilder header = new StringBuilder(LENGDE_PAA_110_HEADER);
        for (int i = 0; i < LENGDE_PAA_110_HEADER; i++) {
            header.replace(i, i + 1, " ");
        }

        header.replace(0, FORMAT.length(), FORMAT);
        header.replace(231, 231 + KILDE.length(), KILDE);
        header.replace(235, 235 + BRUKERID.length(), BRUKERID);
        header.replace(243, 243 + KJOERENR.length(), KJOERENR);

        return header.toString();
    }

    private static void setIdKode(StringBuilder rutine, String idKode) {
        int start = 0;
        checkLength(idKode, 3);
        int length = idKode.length();
        rutine.replace(start, start + length, idKode);
    }

    private static void setIdOff(StringBuilder rutine, String idOff) {
        int start = 3;
        checkLength(idOff, 11);
        int length = idOff.length();
        rutine.replace(start, start + length, idOff);
    }

    private static void setKodeIdenttype(StringBuilder rutine, String kodeIdenttype) {
        int start = 14;
        checkLength(kodeIdenttype, 4);
        int length = kodeIdenttype.length();
        rutine.replace(start, start + length, kodeIdenttype);
    }

    private static void setKodeSamhType(StringBuilder rutine, String kodeSamhType) {
        int start = 18;
        checkLength(kodeSamhType, 4);
        int length = kodeSamhType.length();
        rutine.replace(start, start + length, kodeSamhType);
    }

    private static void setNavn(StringBuilder rutine, String navn) {
        int start = 68;
        checkLength(navn, 40);
        int length = navn.length();
        rutine.replace(start, start + length, navn);
    }

    private static void setOppdater(StringBuilder rutine, String oppdater) {
        int start = 178;
        checkLength(oppdater, 1);
        int length = oppdater.length();
        rutine.replace(start, start + length, oppdater);
    }

    private static void checkLength(String field, int maxLength) {
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Felt " + field + " er for langt. Kan ikke være lenger enn " + maxLength);
        }
    }
}
