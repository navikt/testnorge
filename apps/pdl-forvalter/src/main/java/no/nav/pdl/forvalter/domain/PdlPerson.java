package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.domain.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.pdl.forvalter.domain.falskidentitet.PdlFalskIdentitet;
import no.nav.pdl.forvalter.domain.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private List<PdlKontaktinformasjonForDoedsbo> kontaktinformasjonForDoedsbo;
    private List<PdlUtenlandskIdentifikasjonsnummer> utenlandskIdentifikasjonsnummer;
    private List<PdlFalskIdentitet> falskIdentitet;
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

    private static <T> List<T> initOrGet(List<T> artifact) {
        if (isNull(artifact)) {
            artifact = new ArrayList<T>();
        }
        return artifact;
    }

    public List<PdlBostedadresse> getBostedsadresse() {
        return initOrGet(bostedsadresse);
    }

    public List<PdlKontaktadresse> getKontaktadresse() {
        return initOrGet(kontaktadresse);
    }

    public List<PdlOppholdsadresse> getOppholdsadresse() {
        return initOrGet(oppholdsadresse);
    }

    public List<PdlDeltBosted> getDeltBosted() {
        return initOrGet(deltBosted);
    }

    public List<PdlFamilierelasjon> getForelderBarnRelasjon() {
        return initOrGet(forelderBarnRelasjon);
    }

    public List<PdlAdressebeskyttelse> getAdressebeskyttelse() {
        return initOrGet(adressebeskyttelse);
    }

    public List<PdlFoedsel> getFoedsel() {
        return initOrGet(foedsel);
    }

    public List<PdlDoedsfall> getDoedsfall() {
        return initOrGet(doedsfall);
    }

    public List<PdlKjoenn> getKjoenn() {
        return initOrGet(kjoenn);
    }

    public List<PdlNavn> getNavn() {
        return initOrGet(navn);
    }

    public List<PdlFolkeregisterpersonstatus> getFolkeregisterpersonstatus() {
        return initOrGet(folkeregisterpersonstatus);
    }

    public List<PdlFullmakt> getFullmakt() {
        return initOrGet(fullmakt);
    }

    public List<PdlStatsborgerskap> getStatsborgerskap() {
        return initOrGet(statsborgerskap);
    }

    public List<PdlOpphold> getOpphold() {
        return initOrGet(opphold);
    }

    public List<PdlSivilstand> getSivilstand() {
        return initOrGet(sivilstand);
    }

    public List<PdlTelefonnummer> getTelefonnummer() {
        return initOrGet(telefonnummer);
    }

    public List<PdlInnflytting> getInnflytting() {
        return initOrGet(innflytting);
    }

    public List<PdlUtflytting> getUtflytting() {
        return initOrGet(utflytting);
    }

    public List<PdlForeldreansvar> getForeldreansvar() {
        return initOrGet(foreldreansvar);
    }

    public List<PdlVergemaal> getVergemaal() {
        return initOrGet(vergemaal);
    }

    public List<PdlKontaktinformasjonForDoedsbo> getKontaktinformasjonForDoedsbo() {
        return initOrGet(kontaktinformasjonForDoedsbo);
    }

    public List<PdlUtenlandskIdentifikasjonsnummer> getUtenlandskIdentifikasjonsnummer() {
        return initOrGet(utenlandskIdentifikasjonsnummer);
    }

    public List<PdlFalskIdentitet> getFalskIdentitet() {
        return initOrGet(falskIdentitet);
    }
}
