package no.nav.registre.testnorge.arbeidsforhold.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidstakerDTO {
    String type;
    String offentligIdent;
}
