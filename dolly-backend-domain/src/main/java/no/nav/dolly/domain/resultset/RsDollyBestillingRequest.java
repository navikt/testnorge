package no.nav.dolly.domain.resultset;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyBestillingRequest extends RsDollyBestilling {

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Antall testpersoner som bestilles"
    )
    private int antall;

    @ApiModelProperty(
            position = 2
    )
    private RsTpsfUtvidetBestilling tpsf;
}