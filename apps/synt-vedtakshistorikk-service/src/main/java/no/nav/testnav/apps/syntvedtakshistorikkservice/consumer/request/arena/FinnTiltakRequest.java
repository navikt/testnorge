package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinnTiltakRequest{

    private String personident;
    private String miljoe;

    private List<NyttVedtakTiltak> nyeFinntiltak;

}
