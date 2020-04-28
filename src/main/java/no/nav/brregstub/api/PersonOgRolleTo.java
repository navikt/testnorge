package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonOgRolleTo {

    @ApiModelProperty(dataType = "java.lang.String", example = "010176100000", required = true)
    @NotBlank
    private String fodselsnr;


    @ApiModelProperty(dataType = "java.lang.String", example = "DAGL", required = true)
    @NotBlank
    private String rolle;

    @ApiModelProperty(dataType = "java.lang.String", example = "Daglig leder/ adm direktør", required = true)
    @NotBlank
    private String rollebeskrivelse;

    @ApiModelProperty(dataType = "java.lang.String", example = "Navn")
    @NotBlank
    private String fornavn;

    @ApiModelProperty(dataType = "java.lang.String", example = "Navnesen")
    @NotBlank
    private String slektsnavn;

    private String adresse1;

    private String postnr;

    private String poststed;
    @ApiModelProperty(example = "false", required = true)
    private boolean fratraadt;
}
