package no.nav.registre.bisys.provider.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class SyntetiserBisysRequest {

    @JsonProperty("miljoe")
    private String miljoe;
    @JsonProperty("antallNyeIdenter")
    private int antallNyeIdenter;
}
