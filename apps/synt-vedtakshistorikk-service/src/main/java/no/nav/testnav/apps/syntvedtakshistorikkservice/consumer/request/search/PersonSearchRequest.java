package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.search;

import lombok.*;

import java.util.Collections;
import java.util.List;


@Builder
@Getter
@Setter
@AllArgsConstructor
public class PersonSearchRequest {
    private final String tag = "TESTNORGE";
    private final List<String> excludeTags = Collections.singletonList("DOLLY");
    private final Boolean kunLevende = true;

    private Integer page;
    private Integer pageSize;
    private String randomSeed;
    private AlderSearch alder;
    private RelasjonSearch relasjoner;
    private PersonstatusSearch personstatus;

}
