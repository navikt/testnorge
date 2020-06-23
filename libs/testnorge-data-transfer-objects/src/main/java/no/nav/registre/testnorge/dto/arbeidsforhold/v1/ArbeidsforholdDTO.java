package no.nav.registre.testnorge.dto.arbeidsforhold.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO {
    private final String arbeidsforholdId;
    private final String orgnummer;
    private final Double stillingsprosent;
    private final String yrke;
}
