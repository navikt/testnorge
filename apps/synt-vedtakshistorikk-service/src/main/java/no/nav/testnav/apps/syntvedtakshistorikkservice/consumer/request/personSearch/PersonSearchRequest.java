package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch;

import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
public class PersonSearchRequest {
    private final String tag = "TESTNORGE";
    private final String excludeTag = "DOLLY";
    private final Boolean kunLevende = true;

    private Integer page;
    private Integer pageSize;
    private String randomSeed;
    private AlderSearch alder;
    private BarnSearch barn;
    private PersonstatusSearch personstatus;

}
