package no.nav.dolly.bestilling.aareg.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ArbeidsforholdRespons {

    private String miljoe;
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
