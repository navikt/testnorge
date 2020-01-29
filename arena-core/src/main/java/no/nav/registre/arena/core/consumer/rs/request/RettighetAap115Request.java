package no.nav.registre.arena.core.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

import no.nav.registre.arena.domain.vedtak.NyttVedtakAap;
import no.nav.registre.arena.domain.vedtak.NyttVedtakTillegg;
import no.nav.registre.arena.domain.vedtak.NyttVedtakTiltak;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetAap115Request extends RettighetRequest {

    private List<NyttVedtakAap> nyeAap115;

    @Override public String getArenaForvalterUrlPath() {
        return "/v1/aap115";
    }

    @JsonProperty("nyeAap115")
    @Override public List<NyttVedtakAap> getVedtakAap() {
        return nyeAap115;
    }

    @Override public List<NyttVedtakTiltak> getVedtakTiltak() {
        return Collections.emptyList();
    }

    @Override public List<NyttVedtakTillegg> getVedtakTillegg() {
        return Collections.emptyList();
    }
}
