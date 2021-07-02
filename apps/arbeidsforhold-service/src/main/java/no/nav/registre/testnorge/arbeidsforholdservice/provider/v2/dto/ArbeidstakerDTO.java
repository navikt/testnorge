package no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.Aktoer;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidstakerDTO {

    Aktoer type;
    String offentligIdent;
    String aktoerId;
}
