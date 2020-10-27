package no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsavtaleDTO {
    String arbeidstidsordning;
    Double avtaltArbeidstimeerPerUke; //TODO fiks skrive feil
    Double avtaltArbeidstimerPerUke;
    LocalDate endringsdatoStillingsprosent;
    LocalDate sisteLoennsendringsdato;
    Float stillingsprosent;
    String yrke;
}
