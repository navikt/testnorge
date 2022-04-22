package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RelasjonSearch {
    String harBarn;
}
