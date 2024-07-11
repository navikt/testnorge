package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Navn extends MetadataDTO {
    String fornavn;
    String mellomnavn;
    String etternavn;
}
