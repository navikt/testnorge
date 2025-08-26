package no.nav.dolly.domain.resultset.entity.testgruppe;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTestgruppeMedBestillingId extends RsTestgruppe {

    private List<IdentBestilling> identer;

    public List<IdentBestilling> getIdenter() {

        if (isNull(identer)) {
            identer = new ArrayList<>();
        }
        return identer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IdentBestilling {

        private String ident;
        private boolean iBruk;
        private String beskrivelse;
        @Builder.Default
        private List<Long> bestillingId = new ArrayList<>();
        @Builder.Default
        private List<RsBestillingStatus> bestillinger = new ArrayList<>();
        private Testident.Master master;
    }
}
