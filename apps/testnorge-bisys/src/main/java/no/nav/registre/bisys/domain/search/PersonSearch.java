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
    private static final String TAG = "TESTNORGE";
    private static final Boolean KUN_LEVENDE = true;
    private final List<String> excludeTags = Collections.singletonList("DOLLY");
   
    RelasjonSearch relasjoner;
    private Integer page;
    private Integer pageSize;
    private String randomSeed;
    private FoedselSearch foedsel;

}
