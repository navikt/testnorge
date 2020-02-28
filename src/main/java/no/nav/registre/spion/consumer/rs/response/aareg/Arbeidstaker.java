package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Arbeidstaker {

    private final String type;
    private final String offentligIdent;
    private final String aktoerId;
}
