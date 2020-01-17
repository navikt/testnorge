package no.nav.registre.arena.core.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.vedtak.NyttVedtakAap;
import no.nav.registre.arena.domain.vedtak.NyttVedtakTiltak;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RettighetTiltaksdeltakelseRequest extends RettighetRequest {

    private List<NyttVedtakTiltak> nyeTiltaksdeltakelse;

    @Override public List<NyttVedtakAap> getVedtakAap() {
        return null;
    }

    @JsonProperty("nyeTiltaksdeltakelse")
    @Override public List<NyttVedtakTiltak> getVedtakTiltak() {
        return nyeTiltaksdeltakelse;
    }
}
