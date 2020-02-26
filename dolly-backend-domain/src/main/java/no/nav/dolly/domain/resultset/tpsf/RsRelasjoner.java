package no.nav.dolly.domain.resultset.tpsf;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsRelasjoner {

    @ApiModelProperty(
            position = 1,
            value = "Feltet beskriver liste av \"seriemonogame\" partnere med hovedperson. Siste forhold først, nr to er forrige partner etc"
    )
    private List<RsPartnerRelasjonRequest> partnere;

    @ApiModelProperty(
            position = 2,
            value = "Liste av barn: mine/våre/dine i forhold til hovedperson og angitt partner"
    )
    private List<RsBarnRelasjonRequest> barn;
}
