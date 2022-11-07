package no.nav.registre.sdforvalter.consumer.rs.request.aareg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
//todo sjekke om denne fremdeles er i bruk

@Getter
@Setter
@AllArgsConstructor
public class AaregRequest {

    public AaregRequest(Arbeidsforhold arbeidsforhold, String... environments) {
        this.arbeidsforhold = arbeidsforhold;
        this.environments = Arrays.asList(environments);
    }

    @JsonProperty("arbeidsforhold")
    private Arbeidsforhold arbeidsforhold;

    @JsonProperty("environments")
    private List<String> environments;
}
