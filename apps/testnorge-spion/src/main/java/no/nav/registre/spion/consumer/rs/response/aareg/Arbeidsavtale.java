package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@NoArgsConstructor(force = true)
public class Arbeidsavtale {

    String arbeidstidsordning;
    String yrke;
    float stillingsprosent;
    float antallTimerPrUke;
    float beregnetAntallTimerPrUke;
    LocalDate sistStillingsendring;
    Periode bruksperiode;
    Periode gyldighetsperiode;
    Sporingsinformasjon sporingsinformasjon;

}