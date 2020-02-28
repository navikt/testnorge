package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class AaregResponse {

    private final long navArbeidsforholdId;
    private final String arbeidsforholdId;
    private final Arbeidstaker arbeidstaker;
    private final Arbeidsgiver arbeidsgiver;
    private final Arbeidsgiver opplysningspliktig;
    private final String type;
    private final Ansettelsesperiode ansettelsesperiode;
    private final List<Arbeidsavtale> arbeidsavtaler;
    private final boolean innrapportertEtterAOrdningen;
    private final LocalDate registrert;
    private final LocalDate sistBekreftet;
    private final Sporingsinformasjon sporingsinformasjon;

}