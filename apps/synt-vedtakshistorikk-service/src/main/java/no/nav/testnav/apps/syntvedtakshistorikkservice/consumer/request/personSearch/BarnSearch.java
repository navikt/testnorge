package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BarnSearch {
    Boolean barn;
    Boolean doedfoedtBarn;
}
