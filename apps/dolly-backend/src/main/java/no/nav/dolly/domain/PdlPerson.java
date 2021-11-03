package no.nav.dolly.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.PdlPerson.Rolle.BARN;
import static no.nav.dolly.domain.PdlPerson.Rolle.FAR;
import static no.nav.dolly.domain.PdlPerson.Rolle.MEDMOR;
import static no.nav.dolly.domain.PdlPerson.Rolle.MOR;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlPerson {

    private Data data;

    public enum Gruppe {AKTORID, FOLKEREGISTERIDENT, NPID}

    public enum Rolle {FAR, MOR, MEDMOR, BARN}

    public enum SivilstandType {
        UOPPGITT, UGIFT, GIFT, ENKE_ELLER_ENKEMANN, SKILT, SEPARERT, PARTNER,
        SEPARERT_PARTNER, SKILT_PARTNER, GJENLEVENDE_PARTNER
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private HentIdenter hentIdenter;
        private HentPerson hentPerson;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentIdenter {

        private List<Identer> identer;

        public List<Identer> getIdenter() {
            if (isNull(identer)) {
                identer = new ArrayList<>();
            }
            return identer;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identer {

        private String ident;
        private boolean historisk;
        private Gruppe gruppe;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentPerson {

        private List<Folkeregisteridentifikator> folkeregisteridentifikator;
        private List<Navn> navn;
        private List<Foedsel> foedsel;
        private List<PdlKjoenn> kjoenn;
        private List<ForelderBarnRelasjon> forelderBarnRelasjon;
        private List<Sivilstand> sivilstand;
        private List<Doedsfall> doedsfall;
        private List<UtflyttingFraNorge> utflyttingFraNorge;

        public List<Navn> getNavn() {
            if (isNull(navn)) {
                navn = new ArrayList<>();
            }
            return navn;
        }

        public List<Foedsel> getFoedsel() {
            if (isNull(foedsel)) {
                foedsel = new ArrayList<>();
            }
            return foedsel;
        }

        public List<ForelderBarnRelasjon> getForelderBarnRelasjon() {
            if (isNull(forelderBarnRelasjon)) {
                forelderBarnRelasjon = new ArrayList<>();
            }
            return forelderBarnRelasjon;
        }

        public List<Sivilstand> getSivilstand() {
            if (isNull(sivilstand)) {
                sivilstand = new ArrayList<>();
            }
            return sivilstand;
        }

        public List<Doedsfall> getDoedsfall() {
            if (isNull(doedsfall)) {
                doedsfall = new ArrayList<>();
            }
            return doedsfall;
        }

        public List<UtflyttingFraNorge> getUtflyttingFraNorge() {
            if (isNull(utflyttingFraNorge)) {
                utflyttingFraNorge = new ArrayList<>();
            }
            return utflyttingFraNorge;
        }

        public List<Folkeregisteridentifikator> getFolkeregisteridentifikator() {
            if (isNull(folkeregisteridentifikator)) {
                folkeregisteridentifikator = new ArrayList<>();
            }
            return folkeregisteridentifikator;
        }

        public List<PdlKjoenn> getKjoenn() {
            if (isNull(kjoenn)) {
                kjoenn = new ArrayList<>();
            }
            return kjoenn;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Folkeregisteridentifikator {

        private String identifikasjonsnummer;
        private String type;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Navn {

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlKjoenn {

        private String kjoenn;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Foedsel {

        private LocalDate foedselsdato;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {

        private boolean historisk;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForelderBarnRelasjon {

        private String relatertPersonsIdent;
        private Rolle relatertPersonsRolle;
        private Rolle minRolleForPerson;
        private Metadata metadata;

        public boolean isForelder() {
            return MOR == getRelatertPersonsRolle() ||
                    FAR == getRelatertPersonsRolle() ||
                    MEDMOR == getRelatertPersonsRolle();
        }

        public boolean isBarn() {
            return BARN == getRelatertPersonsRolle();
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sivilstand {

        private SivilstandType type;
        private LocalDate gyldigFraOgMed;
        private String relatertVedSivilstand;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Doedsfall {

        private LocalDate doedsdato;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtflyttingFraNorge {

        private String tilflyttingsland;
        private Folkeregistermetadata folkeregistermetadata;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Folkeregistermetadata {

        private LocalDate gyldighetstidspunkt;
    }
}
