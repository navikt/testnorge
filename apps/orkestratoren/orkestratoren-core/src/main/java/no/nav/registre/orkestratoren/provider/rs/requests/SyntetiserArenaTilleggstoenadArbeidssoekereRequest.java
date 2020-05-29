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
public class SyntetiserArenaTilleggstoenadArbeidssoekereRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;

    @JsonProperty("miljoe")
    private String miljoe;

    @JsonProperty("antallTilsynBarnArbeidssoekere")
    private Integer antallTilsynBarnArbeidssoekere;

    @JsonProperty("antallTilsynFamiliemedlemmerArbeidssoekere")
    private Integer antallTilsynFamiliemedlemmerArbeidssoekere;

    @JsonProperty("antallBoutgifterArbeidssoekere")
    private Integer antallBoutgifterArbeidssoekere;

    @JsonProperty("antallDagligReiseArbeidssoekere")
    private Integer antallDagligReiseArbeidssoekere;

    @JsonProperty("antallFlyttingArbeidssoekere")
    private Integer antallFlyttingArbeidssoekere;

    @JsonProperty("antallLaeremidlerArbeidssoekere")
    private Integer antallLaeremidlerArbeidssoekere;

    @JsonProperty("antallHjemreiseArbeidssoekere")
    private Integer antallHjemreiseArbeidssoekere;

    @JsonProperty("antallReiseObligatoriskSamlingArbeidssoekere")
    private Integer antallReiseObligatoriskSamlingArbeidssoekere;

    @JsonProperty("antallReisestoenadArbeidssoekere")
    private Integer antallReisestoenadArbeidssoekere;
}
