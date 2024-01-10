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
    public enum UtenlandskPersonIdentifikasjon{UtenlandskIdentifikasjonsnummer, TaxIdentificationNumber, SocialSecurityNumber, UtlendingsmyndighetenesIdentifikasjonsnummer}
    public enum IdentitetsgrunnlagStatus {IkkeKontrollert, IngenStatus, Kontrollert}
    public enum Adressebeskyttelse {Fortrolig, StrengtFortrolig}


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
    private Boolean legitimasjonsdokument;
    private Boolean falskIdentitet;
    private Boolean norskStatsborgerskap;
    private Boolean flereStatsborgerskap;
    private Navn navn;


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
    public static class Navn {

        private NavnLengde navnLengde;
        private Boolean flereFornavn;
        private Boolean harMellomnavn;
        private Boolean navnSpesialtegn;
    }

    @Data
    @NoArgsConstructor
    public static class NavnLengde {

        private Integer fraOgMed;
        private Integer tilOgMed;
    }
}
