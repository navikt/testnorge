package no.nav.registre.testnorge.person.consumer.dto.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Foedsel {
    String foedselsdato;
}
