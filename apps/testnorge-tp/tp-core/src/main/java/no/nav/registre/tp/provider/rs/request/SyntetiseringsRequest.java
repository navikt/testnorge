package no.nav.registre.tp.provider.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyntetiseringsRequest {

    @NotNull
    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;
    @NotNull
    @JsonProperty("miljoe")
    private String miljoe;
    @NotNull
    @JsonProperty("antallPersoner")
    private Integer antallPersoner;
}