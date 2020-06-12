package no.nav.registre.arena.core.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakEndreDeltakerstatus;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetEndreDeltakerstatusRequest extends RettighetRequest {

    private List<NyttVedtakEndreDeltakerstatus> nyeEndreDeltakerstatus;

    @Override public String getArenaForvalterUrlPath() {
        return "/v1/endreDeltakerstatus";
    }

    @Override public List<NyttVedtakAap> getVedtakAap() {
        return Collections.emptyList();
    }

    @Override public List<NyttVedtakTiltak> getVedtakTiltak() {
        return Collections.emptyList();
    }

    @Override public List<NyttVedtakTillegg> getVedtakTillegg() {
        return Collections.emptyList();
    }

}
