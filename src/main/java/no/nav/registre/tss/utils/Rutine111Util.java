package no.nav.registre.tss.utils;

import no.nav.registre.tss.consumer.rs.responses.TssSyntMessage;

public class Rutine111Util {
    private static final int LENGDE_PAA_111_MELDING = 203;

    public static String opprett111Rutine(TssSyntMessage message) {
        StringBuilder rutine = new StringBuilder(LENGDE_PAA_111_MELDING);
        for (int i = 0; i < LENGDE_PAA_111_MELDING; i++) {
            rutine.replace(i, i + 1, " ");
        }

        setIdKode(rutine, message.getIdKode());
        setIdAlternativ(rutine, message.getIdAlternativ());
        setKodeAltIdenttype(rutine, message.getKodeAltIdenttype());

        return rutine.toString();
    }

    private static void setIdKode(StringBuilder rutine, String idKode) {
        int start = 0;
        checkLength(idKode, 3);
        int length = idKode.length();
        rutine.replace(start, start + length, idKode);
    }

    private static void setIdAlternativ(StringBuilder rutine, String idAlternativ) {
        int start = 3;
        checkLength(idAlternativ, 11);
        int length = idAlternativ.length();
        rutine.replace(start, start + length, idAlternativ);
    }

    private static void setKodeAltIdenttype(StringBuilder rutine, String kodeAltIdenttype) {
        int start = 14;
        checkLength(kodeAltIdenttype, 4);
        int length = kodeAltIdenttype.length();
        rutine.replace(start, start + length, kodeAltIdenttype);
    }

    private static void checkLength(String field, int maxLength) {
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Felt " + field + " er for langt. Kan ikke være lenger enn " + maxLength);
        }
    }
}
