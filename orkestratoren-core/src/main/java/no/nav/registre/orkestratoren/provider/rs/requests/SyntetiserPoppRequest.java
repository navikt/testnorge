package no.nav.registre.orkestratoren.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SyntetiserPoppRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;
    @JsonProperty("miljoe")
    private String miljoe;
    @JsonProperty("antallIdenter")
    private int antallIdenter;
}
