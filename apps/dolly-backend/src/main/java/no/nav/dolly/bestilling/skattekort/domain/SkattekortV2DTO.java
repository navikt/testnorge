package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkattekortV2DTO {

    private List<SkattekortResponseDTO> data;
    private String errorMessage;

    public List<SkattekortResponseDTO> getData() {

        if (isNull(data)) {
            return new ArrayList<>();
        }
        return data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkattekortResponseDTO {

        private String fnr;

        @EqualsAndHashCode.Exclude
        private String id;

        @EqualsAndHashCode.Exclude
        private String identifikator;

        private Integer inntektsaar;

        @EqualsAndHashCode.Exclude
        private String kilde;

        @EqualsAndHashCode.Exclude
        private String opprettet;

        private String resultatForSkattekort;
        private List<ForskuddstrekkDTO> forskuddstrekkList;
        private List<String> tilleggsopplysningList;
        private LocalDate utstedtDato;

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
