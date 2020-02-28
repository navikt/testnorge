package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Arbeidsgiver {

    private final String type;
    private final String organisasjonsnummer;
}
