package no.nav.registre.aareg.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.aareg.domain.Arbeidsforhold;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArbeidsforholdsResponse {

    @JsonProperty("arbeidsforhold")
    private Arbeidsforhold arbeidsforhold;

    @JsonProperty("arkivreferanse")
    private String arkivreferanse;

    @JsonProperty("environments")
    private List<String> environments;
}
