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
public class RettighetAapRequest extends RettighetRequest {

    private List<NyttVedtakAap> nyeAap;

    @Override public String getArenaForvalterUrlPath() {
        return "/v1/aap";
    }

    @JsonProperty("nyeAap")
    @Override public List<NyttVedtakAap> getVedtakAap() {
        return nyeAap;
    }

    @Override public List<NyttVedtakTiltak> getVedtakTiltak() {
        return Collections.emptyList();
    }

    @Override public List<NyttVedtakTillegg> getVedtakTillegg() {
        return Collections.emptyList();
    }
}
