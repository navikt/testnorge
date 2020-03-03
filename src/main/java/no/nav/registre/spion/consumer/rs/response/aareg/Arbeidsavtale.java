package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@NoArgsConstructor(force = true)
public class Arbeidsavtale {

    private final String arbeidstidsordning;
    private final String yrke;
    private final float stillingsprosent;
    private final float antallTimerPrUke;
    private final float beregnetAntallTimerPrUke;
    private final LocalDate sistStillingsendring;
    private final Periode bruksperiode;
    private final Periode gyldighetsperiode;
    private final Sporingsinformasjon sporingsinformasjon;

}