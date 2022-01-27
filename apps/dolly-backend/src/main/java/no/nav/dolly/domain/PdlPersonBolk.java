package no.nav.dolly.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlPersonBolk {

    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private List<PersonBolk> hentPersonBolk;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonBolk {

        private String ident;
        private Person person;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {

        private List<PdlPerson.Navn> navn;
        private List<PdlPerson.Foedsel> foedsel;
        private List<PdlPerson.ForelderBarnRelasjon> forelderBarnRelasjon;
        private List<PdlPerson.Sivilstand> sivilstand;
        private List<PdlPerson.Doedsfall> doedsfall;
        private List<PdlPerson.UtflyttingFraNorge> utflyttingFraNorge;
        private List<PdlPerson.PdlKjoenn> kjoenn;
        private List<PdlPerson.Folkeregisteridentifikator> folkeregisteridentifikator;
        private List<BostedadresseDTO> bostedsadresse;
        private List<KontaktadresseDTO> kontaktadresse;
        private List<OppholdsadresseDTO> oppholdsadresse;
        private List<InnflyttingDTO> innflytting;
        private List<UtflyttingDTO> utflytting;
        private List<DeltBostedDTO> deltBosted;
        private List<ForeldreansvarDTO> foreldreansvar;
        private List<KontaktinformasjonForDoedsboDTO> kontaktinformasjonForDoedsbo;
        private List<UtenlandskIdentifikasjonsnummerDTO> utenlandskIdentifikasjonsnummer;
        private List<FalskIdentitetDTO> falskIdentitet;
        private List<AdressebeskyttelseDTO> adressebeskyttelse;
        private List<FolkeregisterPersonstatusDTO> folkeregisterpersonstatus;
        private List<TilrettelagtKommunikasjonDTO> tilrettelagtKommunikasjon;
        private List<PdlPerson.Statsborgerskap> statsborgerskap;
        private List<OppholdDTO> opphold;
        private List<TelefonnummerDTO> telefonnummer;
        private List<FullmaktDTO> fullmakt;
        private List<VergemaalDTO> vergemaal;
        private List<SikkerhetstiltakDTO> sikkerhetstiltak;
        private List<DoedfoedtBarnDTO> doedfoedtBarn;

        public List<PdlPerson.Navn> getNavn() {
            if (isNull(navn)) {
                navn = new ArrayList<>();
            }
            return navn;
        }

        public List<PdlPerson.Foedsel> getFoedsel() {
            if (isNull(foedsel)) {
                foedsel = new ArrayList<>();
            }
            return foedsel;
        }

        public List<PdlPerson.ForelderBarnRelasjon> getForelderBarnRelasjon() {
            if (isNull(forelderBarnRelasjon)) {
                forelderBarnRelasjon = new ArrayList<>();
            }
            return forelderBarnRelasjon;
        }

        public List<PdlPerson.Sivilstand> getSivilstand() {
            if(isNull(sivilstand)) {
                sivilstand = new ArrayList<>();
            }
            return sivilstand;
        }

        public List<PdlPerson.Doedsfall> getDoedsfall() {
            if (isNull(doedsfall)) {
                doedsfall = new ArrayList<>();
            }
            return doedsfall;
        }

        public List<PdlPerson.UtflyttingFraNorge> getUtflyttingFraNorge() {
            if (isNull(utflyttingFraNorge)) {
                utflyttingFraNorge = new ArrayList<>();
            }
            return utflyttingFraNorge;
        }

        public List<PdlPerson.PdlKjoenn> getKjoenn() {
            if (isNull(kjoenn)) {
                kjoenn = new ArrayList<>();
            }
            return kjoenn;
        }

        public List<PdlPerson.Folkeregisteridentifikator> getFolkeregisteridentifikator() {
            if (isNull(folkeregisteridentifikator)) {
                folkeregisteridentifikator = new ArrayList<>();
            }
            return folkeregisteridentifikator;
        }

        public List<BostedadresseDTO> getBostedsadresse() {
            if (isNull(bostedsadresse)) {
                bostedsadresse = new ArrayList<>();
            }
            return bostedsadresse;
        }

        public List<KontaktadresseDTO> getKontaktadresse() {
            if (isNull(kontaktadresse)) {
                kontaktadresse = new ArrayList<>();
            }
            return kontaktadresse;
        }

        public List<OppholdsadresseDTO> getOppholdsadresse() {
            if (isNull(oppholdsadresse)) {
                oppholdsadresse = new ArrayList<>();
            }
            return oppholdsadresse;
        }

        public List<InnflyttingDTO> getInnflytting() {
            if (isNull(innflytting)) {
                innflytting = new ArrayList<>();
            }
            return innflytting;
        }

        public List<UtflyttingDTO> getUtflytting() {
            if (isNull(utflytting)) {
                utflytting = new ArrayList<>();
            }
            return utflytting;
        }

        public List<DeltBostedDTO> getDeltBosted() {
            if (isNull(deltBosted)) {
                deltBosted = new ArrayList<>();
            }
            return deltBosted;
        }

        public List<ForeldreansvarDTO> getForeldreansvar() {
            if (isNull(foreldreansvar)) {
                foreldreansvar = new ArrayList<>();
            }
            return foreldreansvar;
        }

        public List<KontaktinformasjonForDoedsboDTO> getKontaktinformasjonForDoedsbo() {
            if (isNull(kontaktinformasjonForDoedsbo)) {
                kontaktinformasjonForDoedsbo = new ArrayList<>();
            }
            return kontaktinformasjonForDoedsbo;
        }

        public List<UtenlandskIdentifikasjonsnummerDTO> getUtenlandskIdentifikasjonsnummer() {
            if (isNull(utenlandskIdentifikasjonsnummer)) {
                utenlandskIdentifikasjonsnummer = new ArrayList<>();
            }
            return utenlandskIdentifikasjonsnummer;
        }

        public List<FalskIdentitetDTO> getFalskIdentitet() {
            if (isNull(falskIdentitet)) {
                falskIdentitet = new ArrayList<>();
            }
            return falskIdentitet;
        }

        public List<AdressebeskyttelseDTO> getAdressebeskyttelse() {
            if (isNull(adressebeskyttelse)) {
                adressebeskyttelse = new ArrayList<>();
            }
            return adressebeskyttelse;
        }

        public List<FolkeregisterPersonstatusDTO> getFolkeregisterpersonstatus() {
            if (isNull(folkeregisterpersonstatus)) {
                folkeregisterpersonstatus = new ArrayList<>();
            }
            return folkeregisterpersonstatus;
        }

        public List<TilrettelagtKommunikasjonDTO> getTilrettelagtKommunikasjon() {
            if (isNull(tilrettelagtKommunikasjon)) {
                tilrettelagtKommunikasjon = new ArrayList<>();
            }
            return tilrettelagtKommunikasjon;
        }

        public List<PdlPerson.Statsborgerskap> getStatsborgerskap() {
            if (isNull(statsborgerskap)) {
                statsborgerskap = new ArrayList<>();
            }
            return statsborgerskap;
        }

        public List<OppholdDTO> getOpphold() {
            if(isNull(opphold)) {
                opphold = new ArrayList<>();
            }
            return opphold;
        }

        public List<TelefonnummerDTO> getTelefonnummer() {
            if (isNull(telefonnummer)) {
                telefonnummer = new ArrayList<>();
            }
            return telefonnummer;
        }

        public List<FullmaktDTO> getFullmakt() {
            if (isNull(fullmakt)) {
                fullmakt = new ArrayList<>();
            }
            return fullmakt;
        }

        public List<VergemaalDTO> getVergemaal() {
            if (isNull(vergemaal))  {
                vergemaal = new ArrayList<>();
            }
            return vergemaal;
        }

        public List<SikkerhetstiltakDTO> getSikkerhetstiltak() {
            if (isNull(sikkerhetstiltak)) {
                sikkerhetstiltak = new ArrayList<>();
            }
            return sikkerhetstiltak;
        }

        public List<DoedfoedtBarnDTO> getDoedfoedtBarn() {
            if (isNull(doedfoedtBarn)) {
                doedfoedtBarn = new ArrayList<>();
            }
            return doedfoedtBarn;
        }
    }
}
