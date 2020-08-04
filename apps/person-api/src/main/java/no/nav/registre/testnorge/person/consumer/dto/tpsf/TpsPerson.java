package no.nav.registre.testnorge.person.consumer.dto.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TpsPerson {
    String ident;
    String foedselsdato;
    String fornavn;
    String mellomnavn;
    String etternavn;
    List<Boadresse> boadresse;
}
