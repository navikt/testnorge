package no.nav.testnav.apps.personservice.consumer.dto.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TpsPerson {
    String ident;
    LocalDateTime foedselsdato;
    String fornavn;
    String mellomnavn;
    String etternavn;
    List<Boadresse> boadresse;
}
