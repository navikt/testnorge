package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetTvungenForvaltningRequest extends RettighetRequest {

    private List<NyttVedtakAap> nyeAatfor;

    @Override public String getArenaForvalterUrlPath() {
        return "/api/v1/aaptvungenforvaltning";
    }

    @JsonProperty("nyeAatfor")
    @Override public List<NyttVedtakAap> getVedtakAap() {
        return nyeAatfor;
    }

    @Override public List<NyttVedtakTiltak> getVedtakTiltak() {
        return Collections.emptyList();
    }

    @Override public List<NyttVedtakTillegg> getVedtakTillegg() {
        return Collections.emptyList();
    }
}
