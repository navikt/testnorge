package no.nav.brregstub.api.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class RsAdresse {

    @ApiModelProperty(example = "Dollyveien 1", required = true)
    @NotBlank private String adresse1;
    private String adresse2;
    private String adresse3;
    private String postnr;
    private String poststed;
    @ApiModelProperty(example = "NO", required = true)
    @NotBlank private String landKode;
    @ApiModelProperty(example = "0301", value = "Påkrevd for roller")
    private String kommunenr;

}
