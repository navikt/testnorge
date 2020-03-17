package no.nav.dolly.domain.resultset;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyBestillingRequest extends RsDollyUtvidetBestilling {

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Antall testpersoner som bestilles"
    )
    private int antall;

    @ApiModelProperty(
            position = 15,
            value = "Navn på malbestillling"
    )
    private String malBestillingNavn;
}