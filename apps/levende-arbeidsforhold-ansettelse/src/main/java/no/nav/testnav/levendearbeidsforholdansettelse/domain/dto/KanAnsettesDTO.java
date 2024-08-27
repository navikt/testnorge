package no.nav.testnav.levendearbeidsforholdansettelse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanAnsettesDTO {

    private String ident;
    private String orgnummer;
    private boolean kanAnsettes;
    private Integer antallEksisterendeArbeidsforhold;
}
