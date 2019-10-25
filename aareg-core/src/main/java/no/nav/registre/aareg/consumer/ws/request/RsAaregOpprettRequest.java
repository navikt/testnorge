package no.nav.registre.aareg.consumer.ws.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.aareg.domain.RsArbeidsforhold;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAaregOpprettRequest {

    private RsArbeidsforhold arbeidsforhold;
    private String arkivreferanse;
    private List<String> environments;
}
