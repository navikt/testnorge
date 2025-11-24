package no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentPerson {
    List<Navn> navn;
    List<Kjoenn> kjoenn;
    List<Foedselsdato> foedselsdato;
    List<Bostedsadresse> bostedsadresse;
    List<Folkeregisteridentifikator> folkeregisteridentifikator;
    List<Folkeregisterpersonstatus> folkeregisterpersonstatus;
    List<MetadataDTO> foedested;
    List<MetadataDTO> doedsfall;
    List<MetadataDTO> sivilstand;
    List<MetadataDTO> oppholdsadresse;
    List<MetadataDTO> kontaktadresse;
    List<MetadataDTO> innflyttingTilNorge;
    List<MetadataDTO> utflyttingFraNorge;
    List<MetadataDTO> vergemaalEllerFremtidsfullmakt;
    List<MetadataDTO> doedfoedtBarn;
    List<MetadataDTO> adressebeskyttelse;
    MetadataDTO falskIdentitet;
    List<MetadataDTO> utenlandskIdentifikasjonsnummer;
    List<MetadataDTO> tilrettelagtKommunikasjon;
    List<MetadataDTO> sikkerhetstiltak;
    List<MetadataDTO> deltBosted;
    List<MetadataDTO> forelderBarnRelasjon;
    List<MetadataDTO> foreldreansvar;
    List<MetadataDTO> kontaktinformasjonForDoedsbo;
    List<MetadataDTO> navspersonidentifikator;
    List<MetadataDTO> statsborgerskap;
    List<MetadataDTO> opphold;
    List<MetadataDTO> fullmakt;

    public List<Navn> getNavn() {

        if (isNull(navn)) {
            navn = new ArrayList<>();
        }
        return navn;
    }

    public List<Foedselsdato> getFoedselsdato() {

        if (isNull(foedselsdato)) {
            foedselsdato = new ArrayList<>();
        }
        return foedselsdato;
    }

    public List<Kjoenn> getKjoenn() {

        if (isNull(kjoenn)) {
            kjoenn = new ArrayList<>();
        }
        return kjoenn;
    }

    public List<Folkeregisteridentifikator> getFolkeregisteridentifikator() {

        if (isNull(folkeregisteridentifikator)) {
            folkeregisteridentifikator = new ArrayList<>();
        }
        return folkeregisteridentifikator;
    }

    public List<Bostedsadresse> getBostedsadresse() {

        if (isNull(bostedsadresse)) {
            bostedsadresse = new ArrayList<>();
        }
        return bostedsadresse;
    }

    public List<Folkeregisterpersonstatus> getFolkeregisterpersonstatus() {

        if (isNull(folkeregisterpersonstatus)) {
            folkeregisterpersonstatus = new ArrayList<>();
        }
        return folkeregisterpersonstatus;
    }

    public List<MetadataDTO> getFoedested() {

        if (isNull(foedested)) {
            foedested = new ArrayList<>();
        }
        return foedested;
    }

    public List<MetadataDTO> getDoedsfall() {

        if (isNull(doedsfall)) {
            doedsfall = new ArrayList<>();
        }
        return doedsfall;
    }

    public List<MetadataDTO> getVergemaalEllerFremtidsfullmakt() {

        if (isNull(vergemaalEllerFremtidsfullmakt)) {
            vergemaalEllerFremtidsfullmakt = new ArrayList<>();
        }
        return vergemaalEllerFremtidsfullmakt;
    }

    public List<MetadataDTO> getDoedfoedtBarn() {

        if (isNull(doedfoedtBarn)) {
            doedfoedtBarn = new ArrayList<>();
        }
        return doedfoedtBarn;
    }

    public List<MetadataDTO> getAdressebeskyttelse() {

        if (isNull(adressebeskyttelse)) {
            adressebeskyttelse = new ArrayList<>();
        }
        return adressebeskyttelse;
    }

    public List<MetadataDTO> getUtenlandskIdentifikasjonsnummer() {

        if (isNull(utenlandskIdentifikasjonsnummer)) {
            utenlandskIdentifikasjonsnummer = new ArrayList<>();
        }
        return utenlandskIdentifikasjonsnummer;
    }

    public List<MetadataDTO> getTilrettelagtKommunikasjon() {

        if (isNull(tilrettelagtKommunikasjon)) {
            tilrettelagtKommunikasjon = new ArrayList<>();
        }
        return tilrettelagtKommunikasjon;
    }

    public List<MetadataDTO> getSikkerhetstiltak() {

        if (isNull(sikkerhetstiltak)) {
            sikkerhetstiltak = new ArrayList<>();
        }
        return sikkerhetstiltak;
    }

    public List<MetadataDTO> getDeltBosted() {

        if (isNull(deltBosted)) {
            deltBosted = new ArrayList<>();
        }
        return deltBosted;
    }

    public List<MetadataDTO> getForelderBarnRelasjon() {

        if (isNull(forelderBarnRelasjon)) {
            forelderBarnRelasjon = new ArrayList<>();
        }
        return forelderBarnRelasjon;
    }

    public List<MetadataDTO> getForeldreansvar() {

        if (isNull(foreldreansvar)) {
            foreldreansvar = new ArrayList<>();
        }
        return foreldreansvar;
    }

    public List<MetadataDTO> getKontaktinformasjonForDoedsbo() {

        if (isNull(kontaktinformasjonForDoedsbo)) {
            kontaktinformasjonForDoedsbo = new ArrayList<>();
        }
        return kontaktinformasjonForDoedsbo;
    }

    public List<MetadataDTO> getNavspersonidentifikator() {

        if (isNull(navspersonidentifikator)) {
            navspersonidentifikator = new ArrayList<>();
        }
        return navspersonidentifikator;
    }

    public List<MetadataDTO> getSivilstand() {

        if (isNull(sivilstand)) {
            sivilstand = new ArrayList<>();
        }
        return sivilstand;
    }

    public List<MetadataDTO> getStatsborgerskap() {

        if (isNull(statsborgerskap)) {
            statsborgerskap = new ArrayList<>();
        }
        return statsborgerskap;
    }

    public List<MetadataDTO> getOppholdsadresse() {

        if (isNull(oppholdsadresse)) {
            oppholdsadresse = new ArrayList<>();
        }
        return oppholdsadresse;
    }

    public List<MetadataDTO> getKontaktadresse() {

        if (isNull(kontaktadresse)) {
            kontaktadresse = new ArrayList<>();
        }
        return kontaktadresse;
    }

    public List<MetadataDTO> getInnflyttingTilNorge() {

        if (isNull(innflyttingTilNorge)) {
            innflyttingTilNorge = new ArrayList<>();
        }
        return innflyttingTilNorge;
    }

    public List<MetadataDTO> getUtflyttingFraNorge() {

        if (isNull(utflyttingFraNorge)) {
            utflyttingFraNorge = new ArrayList<>();
        }
        return utflyttingFraNorge;
    }

    public List<MetadataDTO> getOpphold() {

        if (isNull(opphold)) {
            opphold = new ArrayList<>();
        }
        return opphold;
    }

    public List<MetadataDTO> getFullmakt() {

        if (isNull(fullmakt)) {
            fullmakt = new ArrayList<>();
        }
        return fullmakt;
    }
}
