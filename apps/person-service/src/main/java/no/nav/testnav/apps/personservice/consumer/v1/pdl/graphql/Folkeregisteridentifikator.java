package no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Folkeregisteridentifikator extends MetadataDTO {
    String identifikasjonsnummer;
    String status;
    String type;
}
