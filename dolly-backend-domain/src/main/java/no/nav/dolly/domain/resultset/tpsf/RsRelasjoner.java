package no.nav.dolly.domain.resultset.tpsf;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsRelasjoner {

    @Schema(description = "Feltet beskriver liste av \"seriemonogame\" partnere med hovedperson. Siste forhold først, nr to er forrige partner etc")
    private List<RsPartnerRelasjonRequest> partnere;

    @Schema(description = "Liste av barn: mine/våre/dine i forhold til hovedperson og angitt partner")
    private List<RsBarnRelasjonRequest> barn;

    @Schema(description = "Liste av foreldre til hovedperson")
    private List<RsForeldreRelasjonRequest> foreldre;
}
