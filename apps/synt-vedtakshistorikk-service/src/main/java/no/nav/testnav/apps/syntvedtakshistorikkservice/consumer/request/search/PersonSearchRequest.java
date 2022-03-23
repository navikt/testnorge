package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.search;

import lombok.*;

import java.util.List;


@Builder
@Getter
@Setter
@AllArgsConstructor
public class PersonSearchRequest {
    private String tag;
    private List<String> excludeTags;
    private Boolean kunLevende;
    private Integer page;
    private Integer pageSize;
    private String randomSeed;
    private AlderSearch alder;
    private RelasjonSearch relasjoner;
    private PersonstatusSearch personstatus;

}
