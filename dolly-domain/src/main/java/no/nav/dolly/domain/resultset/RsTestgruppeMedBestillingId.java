package no.nav.dolly.domain.resultset;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RsTestgruppeMedBestillingId extends RsTestgruppe {

    List<IdentBestilling> identer;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentBestilling {

        private String ident;
        private List<Long> bestillingId;
    }
}
