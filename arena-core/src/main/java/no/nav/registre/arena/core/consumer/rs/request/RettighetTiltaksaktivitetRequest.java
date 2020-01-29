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
public class RettighetTiltaksaktivitetRequest extends RettighetRequest {

    private List<NyttVedtakTiltak> nyeTiltaksaktivitet;

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
