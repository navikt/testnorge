package no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO {
    AnsettelsesperiodeDTO ansettelsesperiode;
    String arbeidsforholdId;
    ArbeidsgiverDTO arbeidsgiver;
    List<ArbeidsavtaleDTO> arbeidsavtaler;
    ArbeidstakerDTO arbeidstaker;
    String type;
}
