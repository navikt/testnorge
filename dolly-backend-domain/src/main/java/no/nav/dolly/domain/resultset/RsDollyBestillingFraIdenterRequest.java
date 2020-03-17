package no.nav.dolly.domain.resultset;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(
            position = -2,
            required = true,
            value = "Liste av identer det skal opprettes testpersoner for"
    )
    private List<String> opprettFraIdenter;

    @ApiModelProperty(
            position = 2
    )
    private RsTpsfBasisMedSivilstandBestilling tpsf;

    @ApiModelProperty(
            position = 15,
            value = "Navn på malbestillling"
    )
    private String malBestillingNavn;

    public List<String> getOpprettFraIdenter() {
        if (isNull(opprettFraIdenter)) {
            opprettFraIdenter = new ArrayList<>();
        }
        return opprettFraIdenter;
    }
}