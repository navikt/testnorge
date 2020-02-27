package no.nav.registre.spion.consumer.rs.response.aaregstub;

import java.util.List;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class AaregstubResponse {

    private final String fnr;
    private final List<Arbeidsforhold> arbeidsforhold;
}