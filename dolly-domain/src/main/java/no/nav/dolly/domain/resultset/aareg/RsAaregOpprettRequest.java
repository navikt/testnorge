package no.nav.dolly.domain.resultset.aareg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAaregOpprettRequest {

    private List<String> environments;
    private RsArbeidsforhold arbeidsforhold;
    private String arkivreferanse;

    public List<String> getEnvironments() {
        return environments;
    }
}
