package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Arbeidsgiver {

    String type;
    String organisasjonsnummer;
}
