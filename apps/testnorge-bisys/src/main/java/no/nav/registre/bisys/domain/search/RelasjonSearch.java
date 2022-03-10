package no.nav.registre.bisys.domain.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RelasjonSearch {
    Boolean mor;
    Boolean far;
}
