package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.domain.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PdlPerson implements Serializable {

    private List<PdlFoedsel> foedsel;
    private List<PdlNavn> navn;
    private List<PdlKjoenn> kjoenn;
    private List<PdlBostedadresse> bostedsadresse;
    private List<PdlKontaktadresse> kontaktadresse;
    private List<PdlOppholdsadresse> oppholdsadresse;
    private List<PdlInnflytting> innflytting;
    private List<PdlUtflytting> utflytting;
    private List<PdlDeltBosted> deltBosted;
    private List<PdlFamilierelasjon> forelderBarnRelasjon;
    private List<PdlForeldreansvar> foreldreansvar;
//    private List<PdlKontaktinformasjonForDoedsbo> kontaktinformasjonForDoedsbo;
    private List<PdlUtenlandskIdentifikasjonsnummer> utenlandskIdentifikasjonsnummer;
//    private List<PdlFalskIdentitet> falskIdentitet;
    private List<PdlAdressebeskyttelse> adressebeskyttelse;
    private List<PdlDoedsfall> doedsfall;
    private List<PdlFolkeregisterpersonstatus> folkeregisterpersonstatus;
    //    private List<PdlIdentitetsgrunnlag> identitetsgrunnlag;
//    private List<PdlTilrettelagtKommunikasjon> tilrettelagtKommunikasjon;
//    private List<PdlFolkeregisteridentifikator> folkeregisteridentifikator;
    private List<PdlStatsborgerskap> statsborgerskap;
    //    private List<PdlSikkerhetstiltak> sikkerhetstiltak;
    private List<PdlOpphold> opphold;
    private List<PdlSivilstand> sivilstand;
    private List<PdlTelefonnummer> telefonnummer;
    private List<PdlFullmakt> fullmakt;
    private List<PdlVergemaal> vergemaal;

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

    public List<PdlInnflytting> getInnflytting() {
        if (isNull(innflytting)) {
            innflytting = new ArrayList<>();
        }
        return innflytting;
    }

    public List<PdlUtflytting> getUtflytting() {
        if (isNull(utflytting)) {
            utflytting = new ArrayList<>();
        }
        return utflytting;
    }

    public List<PdlForeldreansvar> getForeldreansvar() {
        if (isNull(foreldreansvar)) {
            foreldreansvar = new ArrayList<>();
        }
        return foreldreansvar;
    }

    public List<PdlVergemaal> getVergemaal() {
        if (isNull(vergemaal)) {
            vergemaal = new ArrayList<>();
        }
        return vergemaal;
    }

//    public List<PdlKontaktinformasjonForDoedsbo> getKontaktinformasjonForDoedsbo() {
//        return initOrGet(kontaktinformasjonForDoedsbo);
//    }

    public List<PdlUtenlandskIdentifikasjonsnummer> getUtenlandskIdentifikasjonsnummer() {
        if (isNull(utenlandskIdentifikasjonsnummer)) {
            utenlandskIdentifikasjonsnummer = new ArrayList<>();
        }
        return utenlandskIdentifikasjonsnummer;
    }

//    public List<PdlFalskIdentitet> getFalskIdentitet() {
//        return initOrGet(falskIdentitet);
//    }
}
