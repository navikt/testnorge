package no.nav.registre.orkestratoren.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyntetiserArenaVedtakshistorikkRequest {

    @JsonProperty("miljoe")
    private String miljoe;

    @JsonProperty("antallVedtakshistorikker")
    private Integer antallVedtakshistorikker;
}
