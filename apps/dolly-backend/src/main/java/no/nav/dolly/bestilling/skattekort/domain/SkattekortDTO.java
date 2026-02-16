package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkattekortDTO {

    private String utstedtDato; // ISO 8601 format (YYYY-MM-DD)
    private Integer inntektsaar;
    private String resultatForSkattekort;
    private List<ForskuddstrekkDTO> forskuddstrekkList;
    private List<String> tilleggsopplysningList;

    public List<ForskuddstrekkDTO> getForskuddstrekkList() {
        if (isNull(forskuddstrekkList)) {
            return new ArrayList<>();
        }
        return forskuddstrekkList;
    }

    public List<String> getTilleggsopplysningList() {
        if (isNull(tilleggsopplysningList)) {
            return new ArrayList<>();
        }
        return tilleggsopplysningList;
    }

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

        private Integer frikortBeloep;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProsentkortDTO {

        private Float prosentSats;
        private Float antallMndForTrekk;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TabellkortDTO {

        private String tabell;
        private Float prosentSats;
        private Float antallMndForTrekk;
    }
}
