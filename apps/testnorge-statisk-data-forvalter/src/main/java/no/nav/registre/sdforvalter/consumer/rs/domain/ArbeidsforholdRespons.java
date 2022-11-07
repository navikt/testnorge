package no.nav.registre.sdforvalter.consumer.rs.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsforholdRespons {

    private String miljo;
    private List<Arbeidsforhold> eksisterendeArbeidsforhold;
    private Arbeidsforhold arbeidsforhold;
    private String arbeidsforholdId;
    private Throwable error;

    public List<Arbeidsforhold> getEksisterendeArbeidsforhold() {

        if (isNull(eksisterendeArbeidsforhold)) {
            eksisterendeArbeidsforhold = new ArrayList<>();
        }
        return eksisterendeArbeidsforhold;
    }
}
