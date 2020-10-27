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

    public ArbeidsforholdDTO(Arbeidsforhold arbeidsforhold) {
        ansettelsesperiode = AnsettelsesperiodeDTO
                .builder()
                .periode(PeriodeDTO
                        .builder()
                        .fom(arbeidsforhold.getFom())
                        .tom(arbeidsforhold.getTom())
                        .build()
                )
                .build();
        arbeidsforholdId = arbeidsforhold.getArbeidsforholdId();
        arbeidsgiver = ArbeidsgiverDTO
                .builder()
                .organisasjonsnummer(arbeidsforhold.getOrgnummer())
                .type("Organisasjon")
                .build();
        arbeidsavtaler = Collections.singletonList(ArbeidsavtaleDTO
                .builder()
                .stillingsprosent(arbeidsforhold.getStillingsprosent())
                .yrke(arbeidsforhold.getYrke())
                .build()
        );
        arbeidstaker = ArbeidstakerDTO.builder().offentligIdent(arbeidsforhold.getIdent()).type("Person").build();
        type = "ordinaertArbeidsforhold";
    }

    AnsettelsesperiodeDTO ansettelsesperiode;
    String arbeidsforholdId;
    ArbeidsgiverDTO arbeidsgiver;
    List<ArbeidsavtaleDTO> arbeidsavtaler;
    ArbeidstakerDTO arbeidstaker;
    String type;
}
