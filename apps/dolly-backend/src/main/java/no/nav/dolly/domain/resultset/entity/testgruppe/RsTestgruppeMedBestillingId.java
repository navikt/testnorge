package no.nav.dolly.domain.resultset.entity.testgruppe;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.jpa.Testident;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
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

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IdentBestilling {

        private String ident;
        private boolean iBruk;
        private String beskrivelse;
        private List<Long> bestillingId;
        private Testident.Master master;
    }
}
