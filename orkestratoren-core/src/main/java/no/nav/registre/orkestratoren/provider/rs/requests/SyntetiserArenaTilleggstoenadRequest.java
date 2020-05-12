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
public class SyntetiserArenaTilleggstoenadRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;

    @JsonProperty("miljoe")
    private String miljoe;

    @JsonProperty("antallBoutgifter")
    private Integer antallBoutgifter;

    @JsonProperty("antallDagligReise")
    private Integer antallDagligReise;

    @JsonProperty("antallFlytting")
    private Integer antallFlytting;

    @JsonProperty("antallLaeremidler")
    private Integer antallLaeremidler;

    @JsonProperty("antallHjemreise")
    private Integer antallHjemreise;

    @JsonProperty("antallReiseObligatoriskSamling")
    private Integer antallReiseObligatoriskSamling;

    @JsonProperty("antallTilsynBarn")
    private Integer antallTilsynBarn;

    @JsonProperty("antallTilsynFamiliemedlemmer")
    private Integer antallTilsynFamiliemedlemmer;
}
