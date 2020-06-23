package no.nav.registre.testnorge.dto.arbeidsforhold.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdListeDTO {
    List<ArbeidsforholdDTO> liste;
}
