package no.nav.dolly.domain.resultset.entity.bestilling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsMalBestillingSimple {

    private List<MalBruker> brukereMedMaler;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MalBruker {

        private String brukernavn;
        private String brukerId;
    }
}
