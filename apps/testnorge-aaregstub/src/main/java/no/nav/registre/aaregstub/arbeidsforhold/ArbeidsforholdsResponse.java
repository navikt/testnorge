package no.nav.registre.aaregstub.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsforhold;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ArbeidsforholdsResponse {

    private Arbeidsforhold arbeidsforhold;
    private String arkivreferanse;
    private ArrayList<String> environments;
}
