package no.nav.registre.hodejegeren.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Aarsakskode {
    TILDELING_DNUMMER("91"),
    INNVANDRING("02"),
    FOEDSELSMELDING("01"),
    FOEDSELSNUMMERKORREKSJON("39"),
    NAVNEENDRING_FOERSTE("06"),
    NAVNEENDRING_MELDING("07"),
    NAVNEENDRING_KORREKSJON("10"),
    VIGSEL("11"),
    SEPERASJON("14"),
    SKILSMISSE("18"),
    FLYTTING_INNEN_KOMMUNEN("24"),
    ANNULERING_FLYTTING_ADRESSEENDRING("25"),
    INNFLYTTING_ANNEN_KOMMUNE("26"),
    ADRESSEENDRING_UTEN_FLYTTING("28"),
    ADRESSEKORREKSJON("29"),
    FARSKAP_MEDMORSKAP("34"),
    ENDRING_STATSBORGERSKAP_BIBEHOLD("35"),
    ENDRING_FAMILIENUMMER("38"),
    ENDRING_FORELDREANSVAR("40"),
    ENDRING_OPPHOLDSTILLATELSE("41"),
    ENDRING_POSTADRESSE_TILLEGGSADRESSE("44"),
    ENDRING_POSTNUMMER_SKOLE_VALG_GRUNNKRETS("47"),
    ADRESSEENDRING_GAB("49"),
    ENDRING_KORREKSJON_FOEDESTED("51"),
    UREGISTRERT_PERSON("56"),
    DOEDSMELDING("43"),
    UTVANDRING("32");

    private final String aarsakskode;

    public static Aarsakskode fromString(String kode) {
        for (Aarsakskode aarsak : Aarsakskode.values()) {
            if (aarsak.aarsakskode.equals(kode)) {
                return aarsak;
            }
        }
        throw new IllegalArgumentException("No enum constant " + Aarsakskode.class.getCanonicalName() + " with value" + kode);
    }
}
