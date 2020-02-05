package no.nav.dolly.bestilling.aareg.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AaregOpprettRequest {

    private List<String> environments;
    private Arbeidsforhold arbeidsforhold;
    private String arkivreferanse;

    public List<String> getEnvironments() {
        return environments;
    }
}
