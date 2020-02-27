package no.nav.registre.spion.consumer.rs.response.aaregstub;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Arbeidsforhold {

    private final AnsettelsesPeriode ansettelsesPeriode;
    private final Arbeidsavtale arbeidsavtale;
    private final Arbeidsgiver arbeidsgiver;
}