package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkattekortDTO {

    private LocalDate utstedtDato;
    private Integer inntektsaar;
    private String resultatForSkattekort;
    private List<ForskuddstrekkDTO> forskuddstrekkList;
    private List<String> tilleggsopplysningList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForskuddstrekkDTO {

        private String trekkode;
        private FrikortDTO frikort;
        private ProsentkortDTO prosentkort;
        private TabellkortDTO trekktabell;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FrikortDTO {

        private String frikortBeloep;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProsentkortDTO {

        private Double prosentSats;
        private Double antallMndForTrekk;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TabellkortDTO {

        private String tabell;
        private Double prosentSats;
        private Double antallMndForTrekk;
    }
}
