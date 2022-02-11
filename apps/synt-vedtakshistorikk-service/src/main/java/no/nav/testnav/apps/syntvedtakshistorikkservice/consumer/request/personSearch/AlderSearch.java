package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AlderSearch {
    Short fra;
    Short til;
}
