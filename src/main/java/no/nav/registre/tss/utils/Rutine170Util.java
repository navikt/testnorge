package no.nav.registre.tss.utils;

import static no.nav.registre.tss.utils.RutineUtil.MELDINGSLENGDE;

import no.nav.registre.tss.consumer.rs.responses.TssSyntMessage;

public class Rutine170Util {

    public static String opprett170Rutine(TssSyntMessage message) {
        StringBuilder rutine = new StringBuilder(MELDINGSLENGDE);
        for (int i = 0; i < MELDINGSLENGDE; i++) {
            rutine.replace(i, i + 1, " ");
        }

        setIdKode(rutine, message.getIdKode());
        setKodeAutortype(rutine, message.getKodeAutortype());
        setDatoAutorisasjonFom(rutine, message.getDatoAutorisasjonFom());
        setDatoAutorisasjonTom(rutine, message.getDatoAutorisasjonTom());

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

    private static void setDatoAutorisasjonFom(StringBuilder rutine, String datoAutorisasjonFom) {
        int start = 47;
        checkLength(datoAutorisasjonFom, 8);
        int length = datoAutorisasjonFom.length();
        rutine.replace(start, start + length, datoAutorisasjonFom);
    }

    private static void setDatoAutorisasjonTom(StringBuilder rutine, String datoAutorisasjonTom) {
        int start = 55;
        checkLength(datoAutorisasjonTom, 8);
        int length = datoAutorisasjonTom.length();
        rutine.replace(start, start + length, datoAutorisasjonTom);
    }

    private static void checkLength(String field, int maxLength) {
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Felt " + field + " er for langt. Kan ikke være lenger enn " + maxLength);
        }
    }
}
