package no.nav.registre.orkestratoren.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyntetiserSkdmeldingerRequest {

    @NotNull
    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;
    @NotNull
    @JsonProperty("miljoe")
    private String miljoe;
    @NotNull
    @JsonProperty("antallMeldingerPerEndringskode")
    private Map<String, Integer> antallMeldingerPerEndringskode;
}
