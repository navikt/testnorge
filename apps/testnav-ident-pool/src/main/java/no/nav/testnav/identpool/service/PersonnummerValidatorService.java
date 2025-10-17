package no.nav.testnav.identpool.service;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

@UtilityClass
public class PersonnummerValidatorService {

    private static final int[] VEKTER_K1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] VEKTER_K2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
    private static final int[] GYLDIG_REST_K1 = {0, 1, 2, 3};
    private static final int GYLDIG_REST_K2 = 0;
    private static final int[] D_NUMMER_SIFRE = {4, 5, 6, 7};
    private static final int[] SYNTETISKE_MAANED_SIFRE = {4, 5, 6, 7, 8, 9};

    /**
     * Validerer et fødsels-eller-d-nummer(1964 og 2032-type) ved å sjekke kontrollsifrene iht.
     * <a href="https://skatteetaten.github.io/folkeregisteret-api-dokumentasjon/nytt-fodselsnummer-fra-2032/">...</a>
     *
     * @param fnrdnr 11-siffret fødsels-eller-D-nummer som skal valideres.
     * @return true hvis fødsels-eller-D-nummer er gyldig, ellers false
     */
    private static boolean validerKontrollsifferFoedselsEllerDnummer(String fnrdnr) {
        final int[] sifre = konverterTilIntArray(fnrdnr);
        final int gittK1 = sifre[9];
        final int gittK2 = sifre[10];

        final int[] grunnlagK1 = Arrays.copyOfRange(sifre, 0, VEKTER_K2.length);
        final int vektetK1 = IntStream.range(0, VEKTER_K1.length)
                .map(i -> grunnlagK1[i] * VEKTER_K1[i])
                .sum();

        final int beregnetRestSifferK1 = (vektetK1 + gittK1) % 11;

        if (Arrays.stream(GYLDIG_REST_K1).noneMatch(siffer -> siffer == beregnetRestSifferK1)) {
            return false;
        }

        final int[] grunnlagK2 = Arrays.copyOfRange(sifre, 0, VEKTER_K2.length);
        final int vektetK2 = IntStream.range(0, VEKTER_K2.length)
                .map(i -> grunnlagK2[i] * VEKTER_K2[i])
                .sum();

        final int beregnetRestSifferK2 = (vektetK2 + gittK2) % 11;

        return beregnetRestSifferK2 == GYLDIG_REST_K2;
    }

    /**
     * Konverterer en streng til et array av heltall.
     *
     * @param streng strengen som skal konverteres
     * @return array av heltall
     */
    private static int[] konverterTilIntArray(String streng) {
        int[] ints = new int[streng.length()];

        for (int i = 0; i < streng.length(); i++) {
            ints[i] = parseInt(streng.substring(i, i + 1));
        }
        return ints;
    }

    /**
     * Validerer at gitt ID har gyldig format og dato før den kaller selve valideringen.
     *
     * @param gittNummer  ID-nummer som skal valideres.
     * @param erSyntetisk Angir om ID-nummeret er syntetisk.
     * @return true hvis ID-nummeret er gyldig, ellers kaster en IllegalArgumentException.
     * @throws IllegalArgumentException hvis ID-nummeret har ugyldig format eller ikke er gyldig bygget opp.
     */
    private static boolean validerInput(String gittNummer, boolean erSyntetisk) {
        boolean gyldigFormat = gittNummer.matches("^\\d{11}$");

        if (!gyldigFormat) {
            throw new IllegalArgumentException("Ugyldig format: ID må være 11 sifre");
        }

        String dato = gittNummer.substring(0, 6);

        if (erDnummer(gittNummer)) {
            int dagSiffer = Character.getNumericValue(dato.charAt(0));
            dato = (dagSiffer - 4) + dato.substring(1, 6);
        }

        if (erSyntetisk) {
            int maanedSiffer = Character.getNumericValue(dato.charAt(2));
            if (Arrays.stream(SYNTETISKE_MAANED_SIFRE).noneMatch(siffer -> siffer == maanedSiffer)) {
                throw new IllegalArgumentException("Ugyldig format: " + gittNummer + " syntetiske nummer må ha 4, 5, 6, 7, 8 eller 9 på indeks 2");
            }
            // utled kalenderdato fra syntetisk nummer
            dato = dato.substring(0, 2) + (maanedSiffer % 2) + dato.substring(3, 6);
        }

        if (!erDatoGyldig(dato)) {
            throw new IllegalArgumentException(
                    "Ugyldig format: " + gittNummer + " har ugyldig dato " + dato + " i formatet ddMMyy");
        }

        if (!validerKontrollsifferFoedselsEllerDnummer(gittNummer)) {
            throw new IllegalArgumentException(
                    "Ugyldig ID: " + gittNummer + " er ikke gyldig bygget opp som " + (erSyntetisk ? "syntetisk" : "reelt") + " nummer");
        }
        return true;
    }

    /**
     * Sjekker om et gitt nummer er et D-nummer.
     *
     * @param gittNummer Nummeret som skal sjekkes.
     * @return true hvis nummeret er et D-nummer, ellers false.
     */
    private static boolean erDnummer(String gittNummer) {
        return Arrays.asList(D_NUMMER_SIFRE).contains(Character.getNumericValue(gittNummer.charAt(0)));
    }

    /**
     * Sjekker om en gitt dato finnes på en kalender. Da århundre ikke lengre vil kunne utledes av
     * 2032-fødselsnumre, antas alle datoer å være etter år 2000.
     *
     * @param dato Datoen som skal sjekkes i formatet ddMMyy.
     * @return true hvis datoen er gyldig, ellers false.
     */
    private static boolean erDatoGyldig(String dato) {
        final String aarhundre = "20";
        final String aar = dato.substring(4, 6);
        final String maaned = dato.substring(2, 4);
        final String dag = dato.substring(0, 2);
        final boolean erSkuddag = "2902".equals(dag + maaned);
        if (erSkuddag && !erSkuddaar(aar)) {
            return false;
        }
        try {
            LocalDate.parse(dag + maaned + aarhundre + aar, DateTimeFormatter.ofPattern("ddMMyyyy"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Utleder om et gitt år er et skuddår basert på kun to sifre. Dette medører at man ikke
     * kan vite hvilket århundre det gjelder, så velger å anse '00' som skuddåret 2000.
     * Dette er grunnet i det ikke lengre vil være mulig å utlede århundre av 2032-fødselsnumre.
     *
     * @param aar året som skal sjekkes i formatet 'yy'.
     * @return true hvis året er et skuddår, ellers false.
     */
    private static boolean erSkuddaar(String aar) {
        return parseInt(aar) % 4 == 0;
    }

    public static String validerFoedselsnummer(String foedselsnummer) {

        boolean erSyntetisk = foedselsnummer.charAt(2) >= '4';
        try {
            validerInput(foedselsnummer, erSyntetisk);
            return "Gyldig ID: " + foedselsnummer + " er gyldig bygget opp som " + (erSyntetisk ? "syntetisk" : "reelt") + " nummer";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
