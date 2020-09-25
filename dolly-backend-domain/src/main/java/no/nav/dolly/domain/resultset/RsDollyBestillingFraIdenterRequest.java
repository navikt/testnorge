package no.nav.dolly.domain.resultset;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBasisMedSivilstandBestilling;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsDollyBestillingFraIdenterRequest extends RsDollyBestilling {

    @Schema(
            required = true,
            description = "Liste av identer det skal opprettes testpersoner for"
    )
    private List<String> opprettFraIdenter;

    private RsTpsfBasisMedSivilstandBestilling tpsf;

    public List<String> getOpprettFraIdenter() {
        if (isNull(opprettFraIdenter)) {
            opprettFraIdenter = new ArrayList<>();
        }
        return opprettFraIdenter;
    }
}