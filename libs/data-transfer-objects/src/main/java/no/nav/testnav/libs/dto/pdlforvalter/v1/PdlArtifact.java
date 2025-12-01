package no.nav.testnav.libs.dto.pdlforvalter.v1;

import lombok.Getter;

@Getter
public enum PdlArtifact {
    PDL_ADRESSEBESKYTTELSE("Adressebeskyttelse"),
    PDL_BOSTEDADRESSE("Bostedsadresse"),
    PDL_DELTBOSTED("DeltBosted"),
    PDL_DOEDFOEDT_BARN("DoedfoedtBarn"),
    PDL_DOEDSFALL("Doedsfall"),
    PDL_FALSK_IDENTITET("FalskIdentitet"),
    PDL_FOEDESTED("Foedested"),
    PDL_FOEDSEL("Foedsel"),
    PDL_FOEDSELSDATO("Foedselsdato"),
    PDL_FOLKEREGISTER_PERSONSTATUS("FolkeregisterPersonstatus"),
    PDL_FORELDREANSVAR("Foreldreansvar"),
    PDL_FORELDRE_BARN_RELASJON("ForelderBarnRelasjon"),
    PDL_FULLMAKT("Fullmakt"),
    PDL_INNFLYTTING("Innflytting"),
    PDL_KJOENN("Kjoenn"),
    PDL_KONTAKTADRESSE("Kontaktadresse"),
    PDL_KONTAKTINFORMASJON_FOR_DODESDBO("KontaktinformasjonForDoedsbo"),
    PDL_NAVN("Navn"),
    PDL_NAVPERSONIDENTIFIKATOR("NavPersonIdentifikator"),
    PDL_OPPHOLD("Opphold"),
    PDL_OPPHOLDSADRESSE("Oppholdsadresse"),
    PDL_OPPRETT_PERSON(""),
    PDL_PERSON_MERGE(""),
    PDL_SIKKERHETSTILTAK("Sikkerhetstiltak"),
    PDL_SIVILSTAND("Sivilstand"),
    PDL_SLETTING(""),
    PDL_SLETTING_HENDELSEID(""),
    PDL_STATSBORGERSKAP("Statsborgerskap"),
    PDL_TELEFONUMMER("Telefonnummer"),
    PDL_TILRETTELAGT_KOMMUNIKASJON("TilrettelagtKommunikasjon"),
    PDL_UTENLANDS_IDENTIFIKASJON_NUMMER("UtenlandskIdentifikasjonsnummer"),
    PDL_UTFLYTTING("Utflytting"),
    PDL_VERGEMAAL("Vergemaal");

    private final String description;

    PdlArtifact(String description) {
        this.description = description;
    }
}
