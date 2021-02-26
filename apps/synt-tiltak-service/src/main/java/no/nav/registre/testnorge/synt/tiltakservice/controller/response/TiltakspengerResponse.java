package no.nav.registre.testnorge.synt.tiltakservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TiltakspengerResponse {

    private String ident;
    private String miljoe;
    private NyttVedtakResponse response;

}
