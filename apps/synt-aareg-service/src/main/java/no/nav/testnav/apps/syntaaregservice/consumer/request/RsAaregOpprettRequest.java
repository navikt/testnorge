package no.nav.testnav.apps.syntaaregservice.consumer.request;

import lombok.*;
import no.nav.testnav.apps.syntaaregservice.domain.aareg.RsArbeidsforhold;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsAaregOpprettRequest {

    private RsArbeidsforhold arbeidsforhold;
    private String arkivreferanse;
    private List<String> environments;
}
