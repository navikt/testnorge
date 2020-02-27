package no.nav.registre.spion.consumer.rs.response.aaregstub;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Arbeidsgiver {

    private final String aktoertype;
    private final String orgnummer;
}