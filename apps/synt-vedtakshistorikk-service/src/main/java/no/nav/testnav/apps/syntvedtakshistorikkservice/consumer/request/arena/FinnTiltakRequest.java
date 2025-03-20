package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinnTiltakRequest{

    private String personident;
    private String miljoe;

    private List<NyttVedtakTiltak> nyeFinntiltak;

}
