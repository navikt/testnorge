package no.nav.testnav.dollysearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private HentIdenter hentIdenter;
    private HentPerson hentPerson;
    private List<String> tags;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentIdenter {
        private List<Identer> identer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identer {
        private String gruppe;
        private String ident;
        private Boolean historisk;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentPerson {
        private List<Navn> navn;
        private List<Foedselsdato> foedselsdato;
        private List<Kjoenn> kjoenn;
        private List<Sivilstand> sivilstand;
        private List<Statsborgerskap> statsborgerskap;
        private List<Folkeregisterpersonstatus> folkeregisterpersonstatus;
        private List<Doedsfall> doedsfall;
        private List<InnflyttingTilNorge> innflyttingTilNorge;
        private List<UtflyttingFraNorge> utflyttingFraNorge;
        private List<ForelderBarnRelasjon> forelderBarnRelasjon;

        public List<ForelderBarnRelasjon> getForelderBarnRelasjon() {

            if (isNull(forelderBarnRelasjon)) {
                forelderBarnRelasjon = new ArrayList<>();
            }
            return forelderBarnRelasjon;
        }

        public List<UtflyttingFraNorge> getUtflyttingFraNorge() {

            if (isNull(utflyttingFraNorge)) {
                utflyttingFraNorge = new ArrayList<>();
            }
            return utflyttingFraNorge;
        }

        public List<InnflyttingTilNorge> getInnflyttingTilNorge() {

            if (isNull(innflyttingTilNorge)) {
                innflyttingTilNorge = new ArrayList<>();
            }
            return innflyttingTilNorge;
        }

        public List<Doedsfall> getDoedsfall() {

            if (isNull(doedsfall)) {
                doedsfall = new ArrayList<>();
            }
            return doedsfall;
        }

        public List<Folkeregisterpersonstatus> getFolkeregisterpersonstatus() {

            if (isNull(folkeregisterpersonstatus)) {
                folkeregisterpersonstatus = new ArrayList<>();
            }
            return folkeregisterpersonstatus;
        }

        public List<Statsborgerskap> getStatsborgerskap() {

            if (isNull(statsborgerskap)) {
                statsborgerskap = new ArrayList<>();
            }
            return statsborgerskap;
        }

        public List<Sivilstand> getSivilstand() {

            if (isNull(sivilstand)) {
                sivilstand = new ArrayList<>();
            }
            return sivilstand;
        }

        public List<Kjoenn> getKjoenn() {

            if (isNull(kjoenn)) {
                kjoenn = new ArrayList<>();
            }
            return kjoenn;
        }

        public List<Foedselsdato> getFoedselsdato() {

            if (isNull(foedselsdato)) {
                foedselsdato = new ArrayList<>();
            }
            return foedselsdato;
        }

        public List<Navn> getNavn() {

            if (isNull(navn)) {
                navn = new ArrayList<>();
            }
            return navn;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Navn {

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Foedselsdato {

        private LocalDate foedselsdato;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kjoenn {

        private KjoennDTO.Kjoenn kjoenn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sivilstand {

        private SivilstandDTO.Sivilstand type;
        private String relatertVedSivilstand;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statsborgerskap {

        private String land;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Folkeregisterpersonstatus {

        private FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus status;
        private Folkeregistermetadata folkeregistermetadata;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Folkeregistermetadata {

        private LocalDate gyldighetstidspunkt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Doedsfall {

        private LocalDate doedsdato;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InnflyttingTilNorge {

        private String fraflyttingsland;
        private String fraflyttingsstedIUtlandet;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UtflyttingFraNorge {

        private String tilflyttingsland;
        private String tilflyttingsstedIUtlandet;
        private LocalDate utflyttingsdato;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ForelderBarnRelasjon {

        private String relatertPersonsIdent;
        private ForelderBarnRelasjonDTO.Rolle relatertPersonsRolle;
        private ForelderBarnRelasjonDTO.Rolle minRolleForPerson;

        public boolean isBarn() {
            return relatertPersonsRolle == ForelderBarnRelasjonDTO.Rolle.BARN;
        }

        public boolean isForelder() {
            return minRolleForPerson == ForelderBarnRelasjonDTO.Rolle.BARN;
        }
    }
}
