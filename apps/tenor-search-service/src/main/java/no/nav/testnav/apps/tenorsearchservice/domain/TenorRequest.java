package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class TenorRequest {

    public enum IdentifikatorType {FNR, DNR, FNR_TIDLIGERE_DNR}

    public enum Kjoenn {Mann, Kvinne}

    public enum Roller {DAGLIG_LEDER}

    public enum Personstatus {Bosatt, Doed, Forsvunnet, Foedselsregistrert, IkkeBosatt, Inaktiv, Midlertidig, Opphoert, Utflyttet}

    public enum Sivilstatus {EnkeEllerEnkemann, Gift, GjenlevendePartner, RegistrertPartner, Separert, SeparertPartner, Skilt, SkiltPartner, Ugift, Uoppgitt}

    public enum UtenlandskPersonIdentifikasjon {UtenlandskIdentifikasjonsnummer, TaxIdentificationNumber, SocialSecurityNumber, UtlendingsmyndighetenesIdentifikasjonsnummer}

    public enum IdentitetsgrunnlagStatus {IkkeKontrollert, IngenStatus, Kontrollert}

    public enum Adressebeskyttelse {Fortrolig, StrengtFortrolig}

    public enum AdresseGradering {Ugradert, Klientadresse, Fortrolig}

    public enum Relasjon {Barn, Far, Medmor, Mor, Partner}

    public enum Hendelse {
        EndringIAdressebeskyttelse, EndringIBostedsadresse, EndringIBostedsadresseUtenFlytting,
        EndringIBrukAvSamiskSpraak, EndringIDeltBosted, EndringIDoedsfall, EndringIFalskIdentitet, EndringIFamilierelasjon,
        EndringIForeldreansvar, EndringIFratattRettsligHandleevne, EndringIFoedsel, EndringIFoedselINorge,
        EndringIIdentifikasjonsnummer, EndringIIdentitetsgrunnlag, EndringIInnflytting, EndringIKjoenn,
        EndringIKontaktinformasjon, EndringIKontaktopplysningerForDoedsbo, EndringILegitimasjonsdokument,
        EndringINavn, EndringIOpphold, EndringIOppholdPaaSvalbard, EndringIOppholdsadresse, EndringIPerson,
        EndringIRettsligHandleevne, EndringISametingetsValgmanntall, EndringISivilstand, EndringIStatsborgerskap,
        EndringIStatus, EndringIUtenlandskPersonidentifikasjon, EndringIUtflytting,
        EndringIUtlendingsmyndighetenesIdentifikasjonsnummer, EndringIVergemaal, PersonErBosatt, PersonErDod,
        PersonErEndretVedSplitting, PersonErGjenopprettetVedSplitting, PersonErOppdatert, PersonErOpphoert,
        PersonErOpphoertSomDublett, PersonErOpprettet, PersonErReaktivert, PersonErUtflyttet,
        PersonErViderefoertSomGjeldendeVedSammenslaaing
    }

    private List<Roller> roller;

    private IdentifikatorType identifikatorType;
    private DatoIntervall foedselsdato;
    private DatoIntervall doedsdato;
    private Kjoenn kjoenn;
    private Personstatus personstatus;
    private Sivilstatus sivilstatus;
    private List<UtenlandskPersonIdentifikasjon> utenlandskPersonIdentifikasjon;
    private IdentitetsgrunnlagStatus identitetsgrunnlagStatus;
    private Adressebeskyttelse adressebeskyttelse;
    private Boolean harLegitimasjonsdokument;
    private Boolean harFalskIdentitet;
    private Boolean harNorskStatsborgerskap;
    private Boolean harFlereStatsborgerskap;
    private Navn navn;
    private Adresser adresser;
    private Relasjoner relasjoner;
    private Hendelser hendelser;

    public List<UtenlandskPersonIdentifikasjon> getUtenlandskPersonIdentifikasjon() {

        if (isNull(utenlandskPersonIdentifikasjon)) {
            utenlandskPersonIdentifikasjon = new ArrayList<>();
        }
        return utenlandskPersonIdentifikasjon;
    }

    public List<Roller> getRoller() {

        if (isNull(roller)) {
            roller = new ArrayList<>();
        }
        return roller;
    }

    private String adresseNavn;

    @Data
    @NoArgsConstructor
    public static class DatoIntervall {

        private LocalDate fra;
        private LocalDate til;
    }

    @Data
    @NoArgsConstructor
    public static class Intervall {

        private Integer fraOgMed;

        private Integer tilOgMed;
    }

    @Data
    @NoArgsConstructor
    public static class Navn {

        private Intervall navnLengde;
        private Boolean harFlereFornavn;
        private Boolean harMellomnavn;
        private Boolean harNavnSpesialtegn;
    }

    @Data
    @NoArgsConstructor
    public static class Adresser {

        private AdresseGradering adresseGradering;
        private Integer kommunenummer;
        private Boolean harBostedsadresse;
        private Boolean harOppholdAnnetSted;
        private Boolean harPostadresseNorge;
        private Boolean harPostadresseUtland;
        private Boolean harKontaktadresseDoedsbo;
        private Boolean harAdresseSpesialtegn;
    }

    @Data
    @NoArgsConstructor
    public static class Relasjoner {

        private Relasjon relasjon;
        private Intervall antallBarn;
        private Boolean harForeldreAnsvar;
        private Intervall relasjonMedFoedselsaar;
        private Boolean harDeltBosted;
        private Boolean harVergemaalEllerFremtidsfullmakt;
    }

    @Data
    @NoArgsConstructor
    public static class Hendelser {

        private Hendelse hendelse;
    }
}
