package no.nav.testnav.libs.data.dollysearchservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.data.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.NavPersonIdentifikatorDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VergemaalDTO;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Document(indexName = "#{@environment.getProperty('open.search.person.index')}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PdlPerson implements Serializable {

    private String ident;
    private Identtype identtype;
    private Boolean standalone;

    @JsonIgnore
    private Boolean isChanged;

    private List<AdressebeskyttelseDTO> adressebeskyttelse;
    private List<BostedadresseDTO> bostedsadresse;
    private List<DeltBostedDTO> deltBosted;
    private List<DoedfoedtBarnDTO> doedfoedtBarn;
    private List<DoedsfallDTO> doedsfall;
    private List<FalskIdentitetDTO> falskIdentitet;
    private List<FoedestedDTO> foedested;
    private List<FoedselDTO> foedsel;
    private List<FoedselsdatoDTO> foedselsdato;
    private List<FolkeregisterPersonstatusDTO> folkeregisterPersonstatus;
    private List<ForelderBarnRelasjonDTO> forelderBarnRelasjon;
    private List<ForeldreansvarDTO> foreldreansvar;
    private List<FullmaktDTO> fullmakt;
    private List<InnflyttingDTO> innflytting;
    private List<KjoennDTO> kjoenn;
    private List<KontaktadresseDTO> kontaktadresse;
    private List<KontaktinformasjonForDoedsboDTO> kontaktinformasjonForDoedsbo;
    private List<NavnDTO> navn;
    private List<NavPersonIdentifikatorDTO> navPersonIdentifikator;
    private List<OppholdDTO> opphold;
    private List<OppholdsadresseDTO> oppholdsadresse;
    private List<SikkerhetstiltakDTO> sikkerhetstiltak;
    private List<SivilstandDTO> sivilstand;
    private List<StatsborgerskapDTO> statsborgerskap;
    private List<TelefonnummerDTO> telefonnummer;
    private List<TilrettelagtKommunikasjonDTO> tilrettelagtKommunikasjon;
    private List<UtenlandskIdentifikasjonsnummerDTO> utenlandskIdentifikasjonsnummer;
    private List<UtflyttingDTO> utflytting;
    private List<VergemaalDTO> vergemaal;

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

    public List<DeltBostedDTO> getDeltBosted() {
        if (isNull(deltBosted)) {
            deltBosted = new ArrayList<>();
        }
        return deltBosted;
    }

    public List<ForelderBarnRelasjonDTO> getForelderBarnRelasjon() {
        if (isNull(forelderBarnRelasjon)) {
            forelderBarnRelasjon = new ArrayList<>();
        }
        return forelderBarnRelasjon;
    }

    public List<AdressebeskyttelseDTO> getAdressebeskyttelse() {
        if (isNull(adressebeskyttelse)) {
            adressebeskyttelse = new ArrayList<>();
        }
        return adressebeskyttelse;
    }

    public List<FoedselDTO> getFoedsel() {
        if (isNull(foedsel)) {
            foedsel = new ArrayList<>();
        }
        return foedsel;
    }

    public List<DoedsfallDTO> getDoedsfall() {
        if (isNull(doedsfall)) {
            doedsfall = new ArrayList<>();
        }
        return doedsfall;
    }

    public List<KjoennDTO> getKjoenn() {
        if (isNull(kjoenn)) {
            kjoenn = new ArrayList<>();
        }
        return kjoenn;
    }

    public List<NavnDTO> getNavn() {
        if (isNull(navn)) {
            navn = new ArrayList<>();
        }
        return navn;
    }

    public List<FolkeregisterPersonstatusDTO> getFolkeregisterPersonstatus() {
        if (isNull(folkeregisterPersonstatus)) {
            folkeregisterPersonstatus = new ArrayList<>();
        }
        return folkeregisterPersonstatus;
    }

    public List<FullmaktDTO> getFullmakt() {
        if (isNull(fullmakt)) {
            fullmakt = new ArrayList<>();
        }
        return fullmakt;
    }

    public List<StatsborgerskapDTO> getStatsborgerskap() {
        if (isNull(statsborgerskap)) {
            statsborgerskap = new ArrayList<>();
        }
        return statsborgerskap;
    }

    public List<OppholdDTO> getOpphold() {
        if (isNull(opphold)) {
            opphold = new ArrayList<>();
        }
        return opphold;
    }

    public List<SivilstandDTO> getSivilstand() {
        if (isNull(sivilstand)) {
            sivilstand = new ArrayList<>();
        }
        return sivilstand;
    }

    public List<TelefonnummerDTO> getTelefonnummer() {
        if (isNull(telefonnummer)) {
            telefonnummer = new ArrayList<>();
        }
        return telefonnummer;
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

    public List<ForeldreansvarDTO> getForeldreansvar() {
        if (isNull(foreldreansvar)) {
            foreldreansvar = new ArrayList<>();
        }
        return foreldreansvar;
    }

    public List<VergemaalDTO> getVergemaal() {
        if (isNull(vergemaal)) {
            vergemaal = new ArrayList<>();
        }
        return vergemaal;
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

    public List<TilrettelagtKommunikasjonDTO> getTilrettelagtKommunikasjon() {
        if (isNull(tilrettelagtKommunikasjon)) {
            tilrettelagtKommunikasjon = new ArrayList<>();
        }
        return tilrettelagtKommunikasjon;
    }

    public List<DoedfoedtBarnDTO> getDoedfoedtBarn() {
        if (isNull(doedfoedtBarn)) {
            doedfoedtBarn = new ArrayList<>();
        }
        return doedfoedtBarn;
    }

    public List<SikkerhetstiltakDTO> getSikkerhetstiltak() {
        if (isNull(sikkerhetstiltak)) {
            sikkerhetstiltak = new ArrayList<>();
        }
        return sikkerhetstiltak;
    }

    public List<FoedestedDTO> getFoedested() {
        if (isNull(foedested)) {
            foedested = new ArrayList<>();
        }
        return foedested;
    }

    public List<FoedselsdatoDTO> getFoedselsdato() {
        if (isNull(foedselsdato)) {
            foedselsdato = new ArrayList<>();
        }
        return foedselsdato;
    }

    public List<NavPersonIdentifikatorDTO> getNavPersonIdentifikator() {
        if (isNull(navPersonIdentifikator)) {
            navPersonIdentifikator = new ArrayList<>();
        }
        return navPersonIdentifikator;
    }
}
