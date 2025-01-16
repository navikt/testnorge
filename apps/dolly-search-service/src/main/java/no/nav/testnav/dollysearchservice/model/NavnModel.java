package no.nav.testnav.dollysearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NavnModel implements WithMetadata {
    String fornavn;
    String mellomnavn;
    String etternavn;
    Metadata metadata;
}
