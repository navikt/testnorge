package no.nav.registre.inntekt.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AltinnRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;

    @JsonProperty("miljoe")
    private String miljoe;

    @JsonProperty("antallIdenter")
    private Integer antallIdenter;

    @JsonProperty("prosentSomStemmerMedInntektskomponenten")
    @ApiModelProperty(value = "0 eller 'null' dersom man ikke ønsker å sende meldingene til inntektskomponenten")
    private Integer prosentSomStemmerMedInntektskomponenten;
}
