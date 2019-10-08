package no.nav.registre.tss.utils.rutine130;

import static no.nav.registre.tss.utils.RutineUtil.MELDINGSLENGDE;

import no.nav.registre.tss.provider.rs.requests.Rutine130Request;

public class Rutine130Util {

    public static String opprett130Rutine(Rutine130Request message) {
        StringBuilder rutine = new StringBuilder(MELDINGSLENGDE);
        for (int i = 0; i < MELDINGSLENGDE; i++) {
            rutine.replace(i, i + 1, " ");
        }

        setIdKode(rutine, message.getIdKode());
        setAvdelingnr(rutine, message.getAvdelingnr());
        setKodeAdressetype(rutine, message.getKodeAdressetype());
        setKodeLand(rutine, message.getKodeLand());
        setKommunenr(rutine, message.getKommunenr());
        setPostnr(rutine, message.getPostnr());
        setDatoAdresseFom(rutine, message.getDatoAdresseFom());
        setDatoAdresseTom(rutine, message.getDatoAdresseTom());
        setGyldigAdresse(rutine, message.getGyldigAdresse());

        return rutine.toString();
    }

    private static void setIdKode(StringBuilder rutine, String idKode) {
        int start = 0;
        checkLength(idKode, 3);
        int length = idKode.length();
        rutine.replace(start, start + length, idKode);
    }

    private static void setAvdelingnr(StringBuilder rutine, String avdelingnr) {
        int start = 3;
        checkLength(avdelingnr, 2);
        int length = avdelingnr.length();
        rutine.replace(start, start + length, avdelingnr);
    }

    private static void setKodeAdressetype(StringBuilder rutine, String kodeAdressetype) {
        int start = 5;
        checkLength(kodeAdressetype, 4);
        int length = kodeAdressetype.length();
        rutine.replace(start, start + length, kodeAdressetype);
    }

    private static void setKodeLand(StringBuilder rutine, String kodeLand) {
        int start = 49;
        checkLength(kodeLand, 4);
        int length = kodeLand.length();
        rutine.replace(start, start + length, kodeLand);
    }

    private static void setKommunenr(StringBuilder rutine, String kommunenr) {
        int start = 53;
        checkLength(kommunenr, 4);
        int length = kommunenr.length();
        rutine.replace(start, start + length, kommunenr);
    }

    private static void setPostnr(StringBuilder rutine, String postnr) {
        int start = 57;
        checkLength(postnr, 4);
        int length = postnr.length();
        rutine.replace(start, start + length, postnr);
    }

    private static void setDatoAdresseFom(StringBuilder rutine, String datoAdresseFom) {
        int start = 61;
        checkLength(datoAdresseFom, 8);
        int length = datoAdresseFom.length();
        rutine.replace(start, start + length, datoAdresseFom);
    }

    private static void setDatoAdresseTom(StringBuilder rutine, String datoAdresseTom) {
        int start = 69;
        checkLength(datoAdresseTom, 8);
        int length = datoAdresseTom.length();
        rutine.replace(start, start + length, datoAdresseTom);
    }

    private static void setGyldigAdresse(StringBuilder rutine, String gyldigAdresse) {
        int start = 77;
        checkLength(gyldigAdresse, 1);
        int length = gyldigAdresse.length();
        rutine.replace(start, start + length, gyldigAdresse);
    }

    private static void checkLength(String field, int maxLength) {
        if (field.length() > maxLength) {
            throw new IllegalArgumentException("Felt " + field + " er for langt. Kan ikke være lenger enn " + maxLength);
        }
    }
}
