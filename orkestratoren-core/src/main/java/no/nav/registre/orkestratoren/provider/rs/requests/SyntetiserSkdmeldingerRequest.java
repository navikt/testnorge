package no.nav.registre.orkestratoren.provider.rs.requests;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SyntetiserSkdmeldingerRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;
    @JsonProperty("miljoe")
    private String miljoe;
    @JsonProperty("antallMeldingerPerEndringskode")
    private Map<String, Integer> antallMeldingerPerEndringskode;
}
