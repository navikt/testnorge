package no.nav.registre.spion.consumer.rs.response.aaregstub;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class AnsettelsesPeriode {

    private final String fom;
    private final String tom;
}