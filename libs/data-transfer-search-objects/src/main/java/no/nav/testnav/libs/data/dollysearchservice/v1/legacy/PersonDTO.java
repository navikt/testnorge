package no.nav.testnav.libs.data.dollysearchservice.v1.legacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {

    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private String kjoenn;
    private String ident;
    private String aktorId;
    private List<String> tags;
    private FoedselsdatoDTO foedselsdato;
    private DoedsfallDTO doedsfall;
    private SivilstandDTO sivilstand;
    private StatsborgerskapDTO statsborgerskap;
    private UtfyttingFraNorgeDTO utfyttingFraNorge;
    private InnflyttingTilNorgeDTO innfyttingTilNorge;
    private ForelderBarnRelasjonDTO forelderBarnRelasjoner;
    private List<FolkeregisterpersonstatusDTO> folkeregisterpersonstatus;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FoedselsdatoDTO {

        private LocalDate foedselsdato;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DoedsfallDTO {

        private LocalDate doedsdato;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SivilstandDTO {

        private String type;
        private String relatertVedSivilstand;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatsborgerskapDTO {

        private List<String> land;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ForelderBarnRelasjonDTO {

        private List<String> barn;
        private List<ForelderDTO> foreldre;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FolkeregisterpersonstatusDTO {

        private String status;
        private LocalDate gyldighetstidspunkt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ForelderDTO {

        private String ident;
        private String rolle;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InnflyttingTilNorgeDTO {

        private String fraflyttingsland;
        private String fraflyttingsstedIUtlandet;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UtfyttingFraNorgeDTO {

        private String tilflyttingsland;
        private String tilflyttingsstedIUtlandet;
        private LocalDate utflyttingsdato;
    }
}