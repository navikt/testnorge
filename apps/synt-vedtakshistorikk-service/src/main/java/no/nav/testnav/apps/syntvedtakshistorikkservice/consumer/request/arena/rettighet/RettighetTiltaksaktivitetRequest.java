package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RettighetTiltaksaktivitetRequest extends RettighetRequest {

    private List<NyttVedtakTiltak> nyeTiltaksaktivitet;

    @Override public String getArenaForvalterUrlPath() {
        return "/api/v1/tiltaksaktivitet";
    }

    @Override public List<NyttVedtakAap> getVedtakAap() {
        return Collections.emptyList();
    }

    @JsonProperty("nyeTiltaksaktivitet")
    @Override public List<NyttVedtakTiltak> getVedtakTiltak() {
        return nyeTiltaksaktivitet;
    }

    @Override public List<NyttVedtakTillegg> getVedtakTillegg() {
        return Collections.emptyList();
    }
}
