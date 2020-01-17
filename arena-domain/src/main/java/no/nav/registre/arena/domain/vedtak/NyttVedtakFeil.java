package no.nav.registre.arena.domain.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NyttVedtakFeil {

    private String personident;
    private String miljoe;

    @JsonAlias({
            "nyAapFeilstatus", "nyAap115Feilstatus", "nyAaunguforFeilstatus", "nyAatforFeilstatus", "nyFritakFeilstatus",
            "nyTiltaksdeltakelseFeilstatus", "nyTiltakspengerFeilstatus"
    })
    private String status;
    private String melding;
}
