package no.nav.registre.orkestratoren.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SyntetiserPoppRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;
    @JsonProperty("miljoe")
    private String miljoe;
    @JsonProperty("antallNyeIdenter")
    private int antallNyeIdenter;
}
