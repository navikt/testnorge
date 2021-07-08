package no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Foedsel {
    LocalDate foedselsdato;
}
