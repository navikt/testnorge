package no.nav.testnav.libs.dto.oppdragservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OppdragResponse {

    private Oppdrag oppdrag;
    private Infomelding infomelding;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Infomelding {

        private String beskrMelding;
    }
}
