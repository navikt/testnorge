package no.nav.testnav.libs.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NyttVedtakFeil {

    private String personident;
    private String miljoe;

    @JsonAlias({
            "nyAapFeilstatus", "nyAap115Feilstatus", "nyAaunguforFeilstatus", "nyAatforFeilstatus", "nyFritakFeilstatus",
            "nyTiltaksdeltakelseFeilstatus", "nyTiltakspengerFeilstatus",
            "nyTilleggsstonadFeilstatus", "nyeFinntiltakFeilstatus"
    })
    private String status;
    private String melding;
}
