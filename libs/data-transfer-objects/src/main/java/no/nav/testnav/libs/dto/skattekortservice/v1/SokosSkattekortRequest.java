package no.nav.testnav.libs.dto.skattekortservice.v1;

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
public class SokosSkattekortRequest {

    private String fnr;
    private SokosSkattekortDTO skattekort;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SokosSkattekortDTO {
        private LocalDate utstedtDato;
        private Integer inntektsaar;
        private String resultatForSkattekort;
        private List<SokosForskuddstrekkDTO> forskuddstrekkList;
        private List<Tilleggsopplysning> tilleggsopplysningList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SokosForskuddstrekkDTO {
        private Trekkode trekkode;
        private Integer frikortBeloep;
        private String tabell;
        private Double prosentSats;
        private Double antallMndForTrekk;
    }
}
