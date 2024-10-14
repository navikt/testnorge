package no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Foedselsdato extends MetadataDTO {

    LocalDate foedselsdato;
}
