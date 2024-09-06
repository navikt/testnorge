package no.nav.registre.testnorge.personsearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FoedselsdatoModel implements WithMetadata {
    LocalDate foedselsdato;
    Metadata metadata;
}
