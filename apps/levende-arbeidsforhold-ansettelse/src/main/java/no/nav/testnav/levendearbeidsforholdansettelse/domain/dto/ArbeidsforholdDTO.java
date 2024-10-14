package no.nav.testnav.levendearbeidsforholdansettelse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsforholdDTO {

    private String ident;
    private List<Arbeidsforhold> arbeidsforhold;
}
