package no.nav.registre.tss.utils;

import no.nav.registre.tss.consumer.rs.responses.TssSyntMessage;

public class Rutine175Util {
    private static final int LENGDE_PAA_175_MELDING = 203;

    public static String opprett175Rutine(TssSyntMessage message) {
        StringBuilder rutine = new StringBuilder(LENGDE_PAA_175_MELDING);
        for (int i = 0; i < LENGDE_PAA_175_MELDING; i++) {
            rutine.replace(i, i + 1, " ");
        }

        setIdKode(rutine, message.getIdKode());
        setKodeAutortype(rutine, message.getKodeAutortype());
        setKodeAutRett(rutine, message.getKodeAutRett());
        setDatoAutRettFom(rutine, message.getDatoAutRettFom());
        setDatoAutRettTom(rutine, message.getDatoAutRettTom());

        return rutine.toString();
    }

    private static void setIdKode(StringBuilder rutine, String idKode) {
        int start = 0;
        checkLength(idKode, 3);
        int length = idKode.length();
        rutine.replace(start, start + length, idKode);
    }

    private static void setKodeAutortype(StringBuilder rutine, String kodeAutortype) {
        int start = 3;
        checkLength(kodeAutortype, 4);
        int length = kodeAutortype.length();
        rutine.replace(start, start + length, kodeAutortype);
    }

    private static void setKodeAutRett(StringBuilder rutine, String kodeAutRett) {
        int start = 7;
        checkLength(kodeAutRett, 4);
        int length = kodeAutRett.length();
        rutine.replace(start, start + length, kodeAutRett);
    }

    private static void setDatoAutRettFom(StringBuilder rutine, String datoAutRettFom) {
        int start = 51;
        checkLength(datoAutRettFom, 8);
        int length = datoAutRettFom.length();
        rutine.replace(start, start + length, datoAutRettFom);
    }

    private static void setDatoAutRettTom(StringBuilder rutine, String datoAutRettTom) {
        int start = 59;
        checkLength(datoAutRettTom, 8);
        int length = datoAutRettTom.length();
        rutine.replace(start, start + length, datoAutRettTom);
    }

    private static void checkLength(String field, int maxLength) {
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Felt " + field + " er for langt. Kan ikke være lenger enn " + maxLength);
        }
    }
}
