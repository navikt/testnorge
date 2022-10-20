package no.nav.dolly.bestilling.aaregrest.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsforholdRespons {

    private String miljo;
    private List<Arbeidsforhold> eksisterendeArbeidsforhold;
    private Arbeidsforhold arbeidsforhold;
    private Throwable error;
}
