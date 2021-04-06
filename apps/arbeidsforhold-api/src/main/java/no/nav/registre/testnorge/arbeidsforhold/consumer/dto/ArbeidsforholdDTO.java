package no.nav.registre.testnorge.arbeidsforhold.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.List;

import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;

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
