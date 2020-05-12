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
public class SyntetiserArenaTiltakRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;

    @JsonProperty("miljoe")
    private String miljoe;

    @JsonProperty("antallTiltaksdeltakelse")
    private Integer antallTiltaksdeltakelse;

    @JsonProperty("antallTiltakspenger")
    private Integer antallTiltakspenger;

    @JsonProperty("antallBarnetillegg")
    private Integer antallBarnetillegg;
}
