package no.nav.registre.testnorge.arena.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetTiltaksdeltakelseRequest extends RettighetRequest {

    private List<NyttVedtakTiltak> nyeTiltaksdeltakelse;

    @Override public String getArenaForvalterUrlPath() {
        return "/v1/tiltaksdeltakelse";
    }

    @Override public List<NyttVedtakAap> getVedtakAap() {
        return Collections.emptyList();
    }

    @JsonProperty("nyeTiltaksdeltakelse")
    @Override public List<NyttVedtakTiltak> getVedtakTiltak() {
        return nyeTiltaksdeltakelse;
    }

    @Override public List<NyttVedtakTillegg> getVedtakTillegg() {
        return Collections.emptyList();
    }
}
