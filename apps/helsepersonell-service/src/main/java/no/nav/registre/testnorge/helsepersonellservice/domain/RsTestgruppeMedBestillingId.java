package no.nav.registre.testnorge.helsepersonellservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RsTestgruppeMedBestillingId {

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
        private Master master;
    }

    public enum Master {PDL, PDLF, TPSF}
}
