package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.FOEDSELSREGISTRERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.DNR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.NPID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonDTO implements Serializable {

    private String ident;
    private Identtype identtype;

    private List<NavnDTO> navn;
    private List<FoedselDTO> foedsel;
    private List<ForelderBarnRelasjonDTO> forelderBarnRelasjon;
    private List<SivilstandDTO> sivilstand;
    private List<DoedsfallDTO> doedsfall;
    private List<BostedadresseDTO> bostedsadresse;
    private List<KontaktadresseDTO> kontaktadresse;
    private List<KjoennDTO> kjoenn;
    private List<OppholdsadresseDTO> oppholdsadresse;
    private List<InnflyttingDTO> innflytting;
    private List<UtflyttingDTO> utflytting;
    private List<DeltBostedDTO> deltBosted;
    private List<ForeldreansvarDTO> foreldreansvar;
    private List<KontaktinformasjonForDoedsboDTO> kontaktinformasjonForDoedsbo;
    private List<UtenlandskIdentifikasjonsnummerDTO> utenlandskIdentifikasjonsnummer;
    private List<FalskIdentitetDTO> falskIdentitet;
    private List<AdressebeskyttelseDTO> adressebeskyttelse;
    private List<FolkeregisterPersonstatusDTO> folkeregisterPersonstatus;
    private List<TilrettelagtKommunikasjonDTO> tilrettelagtKommunikasjon;
    private List<StatsborgerskapDTO> statsborgerskap;
    private List<OppholdDTO> opphold;
    private List<TelefonnummerDTO> telefonnummer;
    private List<FullmaktDTO> fullmakt;
    private List<VergemaalDTO> vergemaal;
    private List<SikkerhetstiltakDTO> sikkerhetstiltak;
    private List<IdentRequestDTO> nyident;
    private List<DoedfoedtBarnDTO> doedfoedtBarn;

    public List<IdentRequestDTO> getNyident() {
        if (isNull(nyident)) {
            nyident = new ArrayList<>();
        }
        return nyident;
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

    @JsonIgnore
    public boolean isStatusIn(FolkeregisterPersonstatus... statuser) {

        return Arrays.asList(statuser).contains(getFolkeregisterPersonstatus().stream()
                .findFirst().orElse(FolkeregisterPersonstatusDTO.builder()
                        .status(FOEDSELSREGISTRERT)
                        .build())
                .getStatus());
    }

    public Identtype getIdenttype() {

        if (isNull(ident)) {
            return null;
        }
        if (parseInt(ident.substring(2, 3)) % 4 >= 2) {
            return NPID;
        } else if (parseInt(ident.substring(0, 1)) >= 4) {
            return DNR;
        } else {
            return FNR;
        }
    }
}
