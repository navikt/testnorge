package no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Folkeregisterpersonstatus extends MetadataDTO {
    String identifikasjonsnummer;
    String status;
    String type;
}
