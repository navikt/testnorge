package no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SyntDTO {
    SyntArbeidsforholdDTO arbeidsforhold;
}
