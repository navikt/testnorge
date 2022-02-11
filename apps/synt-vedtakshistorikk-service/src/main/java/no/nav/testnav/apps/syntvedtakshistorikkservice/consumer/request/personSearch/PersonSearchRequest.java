package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonSearchRequest {
    Integer page = 1;
    Integer pageSize = 100;
    String tag = "TESTNORGE";
    String excludeTag = "DOLLY";
    Boolean kunLevende = true;

    String randomSeed;
    AlderSearch alder;
    BarnSearch barn;
    PersonstatusSearch personstatus;
}
