package no.nav.registre.aareg.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.aareg.consumer.rs.responses.contents.Arbeidsforhold;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsforholdsResponse {

    @JsonProperty("arbeidsforhold")
    private Arbeidsforhold arbeidsforhold;

    @JsonProperty("arkivreferanse")
    private String arkivreferanse;

    @JsonProperty("environments")
    private List<String> environments;
}
