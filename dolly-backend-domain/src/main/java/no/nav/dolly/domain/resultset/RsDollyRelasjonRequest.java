package no.nav.dolly.domain.resultset;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsRelasjoner;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyRelasjonRequest {

    @Schema(required = true,
            description = "Liste av miljøer bestilling(en) gjelder for")
    private List<String> environments;

    @Schema(required = true,
            description = "Identifikasjon av relasjoner som skal opprettes")
    private Tpsf tpsf;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tpsf {

        private RsRelasjoner relasjoner;
    }
}