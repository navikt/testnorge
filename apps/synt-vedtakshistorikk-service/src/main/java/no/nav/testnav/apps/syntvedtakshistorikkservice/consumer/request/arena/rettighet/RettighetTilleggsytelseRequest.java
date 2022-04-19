package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet;

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
public class RettighetTilleggsytelseRequest extends RettighetRequest {

    private List<NyttVedtakTiltak> nyeTilleggsytelser;

    @Override public String getArenaForvalterUrlPath() {
        return "/api/v1/tilleggsytelser";
    }

    @Override public List<NyttVedtakAap> getVedtakAap() {
        return Collections.emptyList();
    }

    @JsonProperty("nyeTilleggsytelser")
    @Override public List<NyttVedtakTiltak> getVedtakTiltak() {
        return nyeTilleggsytelser;
    }

    @Override public List<NyttVedtakTillegg> getVedtakTillegg() {
        return Collections.emptyList();
    }
}
