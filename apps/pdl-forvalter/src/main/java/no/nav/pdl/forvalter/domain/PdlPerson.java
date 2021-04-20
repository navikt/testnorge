package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlPerson implements Serializable {

    private List<PdlBostedadresse> bostedsadresse;
    private List<PdlKontaktadresse> kontaktadresse;
    private List<PdlOppholdsadresse> oppholdsadresse;
    private List<PdlDeltBosted> deltBosted;
    private List<PdlFamilierelasjon> forelderBarnRelasjon;
//    private List<PdlKontaktinformasjonForDoedsbo>
//    private List<PdlUtenlandskIdentifikasjonsnummer> utenlandskIdentifikasjonsnummer
    private List<PdlAdressebeskyttelse> adressebeskyttelse;
    private List<PdlFoedsel> foedsel;
    private List<PdlDoedsfall> doedsfall;
    private List<PdlKjoenn> kjoenn;
    private List<PdlNavn> navn;
    private List<PdlFolkeregisterpersonstatus> folkeregisterpersonstatus;
//    private List<PdlIdentitetsgrunnlag> identitetsgrunnlag;
//    private List<PdlTilrettelagtKommunikasjon> tilrettelagtKommunikasjon;
    private List<PdlFullmakt> fullmakt;
//    private List<PdlFolkeregisteridentifikator> folkeregisteridentifikator;
    private List<PdlStatsborgerskap> statsborgerskap;
//    private List<PdlSikkerhetstiltak> sikkerhetstiltak;
    private List<PdlOpphold> opphold;
    private List<PdlSivilstand> sivilstand;
    private List<PdlTelefonnummer> telefonnummer;

    public List<PdlBostedadresse> getBostedsadresse() {
        if (isNull(bostedsadresse)) {
            bostedsadresse = new ArrayList<>();
        }
        return bostedsadresse;
    }

    public List<PdlKontaktadresse> getKontaktadresse() {
        if (isNull(kontaktadresse)) {
            kontaktadresse = new ArrayList<>();
        }
        return kontaktadresse;
    }

    public List<PdlOppholdsadresse> getOppholdsadresse() {
        if (isNull(oppholdsadresse)) {
            oppholdsadresse = new ArrayList<>();
        }
        return oppholdsadresse;
    }

    public List<PdlDeltBosted> getDeltBosted() {
        if (isNull(deltBosted)) {
            deltBosted = new ArrayList<>();
        }
        return deltBosted;
    }

    public List<PdlFamilierelasjon> getForelderBarnRelasjon() {
        if (isNull(forelderBarnRelasjon)) {
            forelderBarnRelasjon = new ArrayList<>();
        }
        return forelderBarnRelasjon;
    }

    public List<PdlAdressebeskyttelse> getAdressebeskyttelse() {
        if (isNull(adressebeskyttelse)) {
            adressebeskyttelse = new ArrayList<>();
        }
        return adressebeskyttelse;
    }

    public List<PdlFoedsel> getFoedsel() {
        if (isNull(foedsel)) {
            foedsel = new ArrayList<>();
        }
        return foedsel;
    }

    public List<PdlDoedsfall> getDoedsfall() {
        if (isNull(doedsfall)) {
            doedsfall = new ArrayList<>();
        }
        return doedsfall;
    }

    public List<PdlKjoenn> getKjoenn() {
        if (isNull(kjoenn)) {
            kjoenn = new ArrayList<>();
        }
        return kjoenn;
    }

    public List<PdlNavn> getNavn() {
        if (isNull(navn)) {
            navn = new ArrayList<>();
        }
        return navn;
    }

    public List<PdlFolkeregisterpersonstatus> getFolkeregisterpersonstatus() {
        if (isNull(folkeregisterpersonstatus)) {
            folkeregisterpersonstatus = new ArrayList<>();
        }
        return folkeregisterpersonstatus;
    }

    public List<PdlFullmakt> getFullmakt() {
        if (isNull(fullmakt)) {
            fullmakt = new ArrayList<>();
        }
        return fullmakt;
    }

    public List<PdlStatsborgerskap> getStatsborgerskap() {
        if (isNull(statsborgerskap)) {
            statsborgerskap = new ArrayList<>();
        }
        return statsborgerskap;
    }

    public List<PdlOpphold> getOpphold() {
        if (isNull(opphold)) {
            opphold = new ArrayList<>();
        }
        return opphold;
    }

    public List<PdlSivilstand> getSivilstand() {
        if (isNull(sivilstand)) {
            sivilstand = new ArrayList<>();
        }
        return sivilstand;
    }

    public List<PdlTelefonnummer> getTelefonnummer() {
        if (isNull(telefonnummer)) {
            telefonnummer = new ArrayList<>();
        }
        return telefonnummer;
    }
}
