package no.nav.registre.arena.domain.historikk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.vedtak.NyttVedtakAap;

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
}
