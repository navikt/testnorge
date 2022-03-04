package no.nav.registre.bisys.consumer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class PersonSearchRequest {
    private static final String tag = "TESTNORGE";
    private final List<String> excludeTags = Collections.singletonList("DOLLY");
    private static final Boolean kunLevende = true;

    private Integer page;
    private Integer pageSize;
    private String randomSeed;
    private FoedselSearch foedsel;
    RelasjonSearch relasjoner;

}
