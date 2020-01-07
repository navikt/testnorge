package no.nav.dolly.domain.resultset;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsRelasjoner;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyRelationRequest {

    @ApiModelProperty(
            position = 1,
            value = "Liste av miljøer bestilling(en) gjelder for",
            required = true
    )
    private List<String> environments;

    @ApiModelProperty(
            position = 1,
            value = "Identifikasjon av relasjoner som skal opprettes",
            required = true
    )
    private RsRelasjoner relasjoner;
}