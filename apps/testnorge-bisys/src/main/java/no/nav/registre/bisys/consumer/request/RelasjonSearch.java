package no.nav.registre.bisys.consumer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RelasjonSearch {
    Boolean barn;
    Boolean doedfoedtBarn;
    Boolean mor;
    Boolean far;
}
