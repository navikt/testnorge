package no.nav.registre.bisys.domain.search;

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
public class PersonSearch {
    private final String tag = "TESTNORGE";
    private final List<String> excludeTags = Collections.singletonList("DOLLY");
    private final Boolean kunLevende = true;

    private Integer page;
    private Integer pageSize;
    private String randomSeed;
    private FoedselSearch foedsel;
    RelasjonSearch relasjoner;

}
