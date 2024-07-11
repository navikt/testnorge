package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Folkeregisterpersonstatus extends MetadataDTO {
    String identifikasjonsnummer;
    String status;
    String type;
}
