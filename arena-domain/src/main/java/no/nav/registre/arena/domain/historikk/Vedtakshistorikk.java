package no.nav.registre.arena.domain.historikk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.rettighet.NyttVedtak;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vedtakshistorikk {

    @JsonProperty("AAP")
    private List<NyttVedtak> aap;

    @JsonProperty("11_5")
    private List<NyttVedtak> aap115;

    @JsonProperty("AAUNGUFOR")
    private List<NyttVedtak> ungUfoer;

    @JsonProperty("AATFOR")
    private List<NyttVedtak> tvungenForvaltning;

    @JsonProperty("FRI_MK")
    private List<NyttVedtak> fritakMeldekort;
}
