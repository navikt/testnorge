package no.nav.registre.orkestratoren.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntetiserTpRequest {

    @NotNull
    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;
    @NotNull
    @JsonProperty("miljoe")
    private String miljoe;
    @NotNull
    @JsonProperty("antallPersoner")
    private int antallPersoner;

}
