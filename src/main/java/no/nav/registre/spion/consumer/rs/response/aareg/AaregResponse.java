package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AaregResponse {

    long navArbeidsforholdId;
    String arbeidsforholdId;
    Arbeidstaker arbeidstaker;
    Arbeidsgiver arbeidsgiver;
    Arbeidsgiver opplysningspliktig;
    String type;
    Ansettelsesperiode ansettelsesperiode;
    List<Arbeidsavtale> arbeidsavtaler;
    boolean innrapportertEtterAOrdningen;
    LocalDate registrert;
    LocalDate sistBekreftet;
    Sporingsinformasjon sporingsinformasjon;

}