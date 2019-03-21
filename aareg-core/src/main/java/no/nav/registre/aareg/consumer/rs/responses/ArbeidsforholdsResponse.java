package no.nav.registre.aareg.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import no.nav.registre.aareg.consumer.rs.responses.contents.Arbeidsforhold;

@Data
@Builder
public class ArbeidsforholdsResponse {

    @JsonProperty("arbeidsforhold")
    private Arbeidsforhold arbeidsforhold;

    @JsonProperty("arkivreferanse")
    private String arkivreferanse;

    @JsonProperty("environments")
    private List<String> environments;
}
