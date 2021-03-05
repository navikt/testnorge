package no.nav.registre.testnorge.personsearchservice.adapter.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class InnflyttingTilNorge implements WithMetadata {
    Metadata metadata;
    String fraflyttingsland;
    String fraflyttingsstedIUtlandet;
}
