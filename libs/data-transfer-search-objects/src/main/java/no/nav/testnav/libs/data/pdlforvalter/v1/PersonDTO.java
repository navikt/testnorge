package no.nav.testnav.libs.data.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static no.nav.testnav.libs.data.pdlforvalter.v1.Identtype.DNR;
import static no.nav.testnav.libs.data.pdlforvalter.v1.Identtype.FNR;
import static no.nav.testnav.libs.data.pdlforvalter.v1.Identtype.NPID;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonDTO implements Serializable {

    protected static final int[] WEIGHTS = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    protected static final List<Integer> VALIDS = java.util.List.of(1, 2, 3);

    private String ident;
    private Identtype identtype;
    private Boolean id2032;
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

    private List<IdentRequestDTO> nyident;

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

    public Boolean getId2032() {

        return isId2032(ident);
    }

    @JsonIgnore
    public boolean isStrengtFortrolig() {

        return getAdressebeskyttelse().stream().anyMatch(AdressebeskyttelseDTO::isStrengtFortrolig);
    }

    @JsonIgnore
    public boolean isStandalone() {

        return isTrue(standalone);
    }

    @JsonIgnore
    public boolean isNotChanged() {

        return isNotTrue(isChanged) && Arrays.stream(this.getClass().getMethods())
                .filter(method -> method.getName().startsWith("get"))
                .filter(method -> method.getReturnType().equals(List.class))
                .map(method -> {
                    try {
                        return method.invoke(this);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return emptyList();
                    }
                })
                .map(result -> (List<? extends DbVersjonDTO>) result)
                .flatMap(Collection::stream)
                .noneMatch(entity -> isTrue(entity.getIsNew()));
    }

    public static boolean isId2032(String ident) {

        if (isBlank(ident) || ident.length() != 11 || !ident.chars().allMatch(Character::isDigit)) {
            return false;
        }

        var vektetK1 = IntStream.range(0, WEIGHTS.length)
                .map(i -> Character.getNumericValue(ident.charAt(i)) * WEIGHTS[i])
                .sum();

        final int beregnetRestSifferK1 = (vektetK1 + Character.getNumericValue(ident.charAt(9))) % 11;

        return VALIDS.contains(beregnetRestSifferK1);
    }
}