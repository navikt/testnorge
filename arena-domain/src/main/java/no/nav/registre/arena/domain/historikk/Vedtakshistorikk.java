package no.nav.registre.arena.domain.historikk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.rettighet.NyRettighet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vedtakshistorikk {

    @JsonProperty("AAP")
    private List<NyRettighet> aap;

    @JsonProperty("AAUNGUFOR")
    private List<NyRettighet> ungUfoer;

    @JsonProperty("AATFOR")
    private List<NyRettighet> tvungenForvaltning;

    @JsonProperty("FRI_MK")
    private List<NyRettighet> fritakMeldekort;
}
