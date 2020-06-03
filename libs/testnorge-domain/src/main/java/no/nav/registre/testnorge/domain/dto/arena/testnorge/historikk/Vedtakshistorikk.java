package no.nav.registre.testnorge.domain.dto.arena.testnorge.historikk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vedtakshistorikk {

    @JsonProperty("AAP")
    private List<NyttVedtakAap> aap;

    @JsonProperty("AA115")
    private List<NyttVedtakAap> aap115;

    @JsonProperty("AAUNGUFOR")
    private List<NyttVedtakAap> ungUfoer;

    @JsonProperty("AATFOR")
    private List<NyttVedtakAap> tvungenForvaltning;

    @JsonProperty("FRI_MK")
    private List<NyttVedtakAap> fritakMeldekort;

    @JsonProperty("BASI")
    private List<NyttVedtakTiltak> tiltakspenger;

    @JsonProperty("BTIL")
    private List<NyttVedtakTiltak> barnetillegg;
}
