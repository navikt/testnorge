package no.nav.testnav.dollysearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;

import java.time.LocalDate;
import java.util.List;

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
