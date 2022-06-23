package no.nav.registre.bisys.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FoedselModel implements WithMetadata {
    LocalDate foedselsdato;
    Metadata metadata;
}
