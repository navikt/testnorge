package no.nav.dolly.bestilling.aareg.domain;

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
public class ArbeidsforholdEksistens {

    private List<Arbeidsforhold> nyeArbeidsforhold;
    private List<Arbeidsforhold> eksisterendeArbeidsforhold;
}
