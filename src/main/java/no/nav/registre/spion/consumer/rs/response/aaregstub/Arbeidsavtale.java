package no.nav.registre.spion.consumer.rs.response.aaregstub;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Arbeidsavtale {

    private final Integer antallKonverterteTimer;
    private final String arbeidstidsordning;
    private final String avloenningstype;
    private final Double avtaltArbeidstimerPerUke;
    private final String endringsdatoStillingsprosent;
    private final Double stillingsprosent;
    private final String yrke;
}