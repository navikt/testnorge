package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonSearchRequest {
    Pageing pageing;
    String tag;
    String excludeTag;
    String randomSeed;
    Boolean kunLevende;
    BarnSearch barn;
    PersonstatusSearch personstatus;
}
