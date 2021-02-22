package no.nav.registre.testnorge.domain.dto.arena.testnorge.request;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetTilleggRequest extends RettighetRequest {

    private List<NyttVedtakTillegg> nyeTilleggsstonad;

    @Override public String getArenaForvalterUrlPath() {
        return "/v1/tilleggsstonad";
    }

    @Override public List<NyttVedtakAap> getVedtakAap() {
        return Collections.emptyList();
    }

    @Override public List<NyttVedtakTiltak> getVedtakTiltak() {
        return Collections.emptyList();
    }

    @JsonProperty("nyeTilleggsstonad")
    public List<NyttVedtakTillegg> getVedtakTillegg() {
        return nyeTilleggsstonad;
    }
}
