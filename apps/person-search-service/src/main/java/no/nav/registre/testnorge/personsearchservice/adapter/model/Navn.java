package no.nav.registre.testnorge.personsearchservice.adapter.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Navn implements WithMetadata {
    String fornavn;
    String mellomnavn;
    String etternavn;
    Metadata metadata;
}
