package no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto;

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
    Float stillingsprosent;
    String yrke;
    String arbeidstidsordning;
    String ansettelsesform;
    String fartsomraade;
    String skipsregister;
    String skipstype;
    Float antallTimerPrUke;
    LocalDate sistLoennsendring;
    LocalDate sistStillingsendring;
}
