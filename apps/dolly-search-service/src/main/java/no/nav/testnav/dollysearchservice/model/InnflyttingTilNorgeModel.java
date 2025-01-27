package no.nav.testnav.dollysearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class InnflyttingTilNorgeModel implements WithMetadata {
    Metadata metadata;
    String fraflyttingsland;
    String fraflyttingsstedIUtlandet;
}
