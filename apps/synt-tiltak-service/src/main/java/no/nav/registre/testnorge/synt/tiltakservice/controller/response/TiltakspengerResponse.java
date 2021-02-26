package no.nav.registre.testnorge.synt.tiltakservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TiltakspengerMedDeltakelse {

    private NyttVedtakTiltak tiltakspenger;
    private NyttVedtakTiltak tiltaksdeltakelse;
    private String deltakerstatus;

}
