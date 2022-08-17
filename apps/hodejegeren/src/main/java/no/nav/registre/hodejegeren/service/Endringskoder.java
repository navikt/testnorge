package no.nav.registre.hodejegeren.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Endringskode er en samling av kode-verdier som identifiserer en skdmelding.
 * Endringsmelding er firesifret. Sifferposisjon:
 * * 1-2: Årsakskode
 * * 3: Transaksjonskode
 * * 4: Tildelingskode
 */
@Getter
@AllArgsConstructor
public enum Endringskoder {
    TILDELING_DNUMMER("9110"),
    INNVANDRING("0211"),
    FOEDSELSMELDING("0110"),
    FOEDSELSNUMMERKORREKSJON("3910"),
    NAVNEENDRING_FOERSTE("0610"),
    NAVNEENDRING_MELDING("0710"),
    NAVNEENDRING_KORREKSJON("1010"),
    VIGSEL("1110"),
    SEPERASJON("1410"),
    SKILSMISSE("1810"),
    FLYTTING_INNEN_KOMMUNEN("2410"),
    ANNULERING_FLYTTING_ADRESSEENDRING("2510"),
    INNFLYTTING_ANNEN_KOMMUNE("2610"),
    ADRESSEENDRING_UTEN_FLYTTING("2810"),
    ADRESSEKORREKSJON("2910"),
    FARSKAP_MEDMORSKAP("3410"),
    ENDRING_STATSBORGERSKAP_BIBEHOLD("3510"),
    ENDRING_FAMILIENUMMER("3810"),
    ENDRING_FORELDREANSVAR("4010"),
    ENDRING_OPPHOLDSTILLATELSE("4110"),
    ENDRING_POSTADRESSE_TILLEGGSADRESSE("4410"),
    ENDRING_POSTNUMMER_SKOLE_VALG_GRUNNKRETS("4710"),
    KOMMUNEREGULERING("4810"),
    ADRESSEENDRING_GAB("4910"),
    ENDRING_KORREKSJON_FOEDESTED("5110"),
    UREGISTRERT_PERSON("5610"),
    ENDRING_DUF_NUMMER("8110"),
    KORREKSJON_FAMILIEOPPLYSNINGER("9810"),
    DOEDSMELDING("4310"),
    UTVANDRING("3210");

    private String endringskode;

    public String getAarsakskode() {
        return endringskode.substring(0, 2);
    }

    public String getTranskode() {
        return endringskode.substring(2, 3);
    }

    public static Endringskoder getEndringskodeFraVerdi(String endringskodeVerdi) {
        for (Endringskoder endringskode : values()) {
            if (endringskode.endringskode.equals(endringskodeVerdi)) {
                return endringskode;
            }
        }
        throw new IllegalArgumentException(endringskodeVerdi);
    }
}
