package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Foedsel extends MetadataDTO {

    LocalDate foedselsdato;
}
