package no.nav.registre.testnorge.arbeidsforhold.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdoversikterDTO {
    List<ArbeidsforholdoversiktDTO> arbeidsforholdoversikter;
}
