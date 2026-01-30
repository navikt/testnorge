package no.nav.testnav.libs.dto.skattekortservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        private String utstedtDato;
        private Integer inntektsaar;
        private String resultatForSkattekort;
        private List<Forskuddstrekk> forskuddstrekkList;
        private List<Tilleggsopplysning> tilleggsopplysningList;
    }
}
