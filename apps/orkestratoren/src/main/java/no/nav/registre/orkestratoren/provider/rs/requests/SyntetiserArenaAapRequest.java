package no.nav.registre.orkestratoren.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyntetiserArenaAapRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;

    @JsonProperty("miljoe")
    private String miljoe;

    @JsonProperty("antallAap")
    private Integer antallAap;

    @JsonProperty("antallUngUfoer")
    private Integer antallUngUfoer;

    @JsonProperty("antallTvungenForvaltning")
    private Integer antallTvungenForvaltning;

    @JsonProperty("antallFritakMeldekort")
    private Integer antallFritakMeldekort;
}
