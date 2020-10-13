package no.nav.dolly.domain.resultset.tpsf;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsSimpleRelasjoner {

    @Schema(description = "Deprecated. Benytt partnere i stedet. Feltet er tilsted pga. bakover-kompabilitet.")
    private RsPartnerRequest partner;

    @Schema(description = "Feltet beskriver liste av \"seriemonogame\" partnere med hovedperson. Siste forhold først, nr to er forrige partner etc")
    private List<RsPartnerRequest> partnere;

    @Schema(description = "Liste av barn: mine/våre/dine i forhold til hovedperson og angitt partner")
    private List<RsBarnRequest> barn;

    public List<RsPartnerRequest> getPartnere() {
        if (isNull(partnere)) {
            partnere = new ArrayList<>();
        }
        return partnere;
    }

    public List<RsBarnRequest> getBarn() {
        if (isNull(barn)) {
            barn = new ArrayList<>();
        }
        return barn;
    }
}
