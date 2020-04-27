package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonOgRolleTo {

    @ApiModelProperty(dataType = "java.lang.String", example = "010176100000", required = true)
    private String fodselsnr;
    @ApiModelProperty(dataType = "java.lang.String", example = "DAGL", required = true)
    private String rolle;
    @ApiModelProperty(dataType = "java.lang.String", example = "Daglig leder/ adm direktør", required = true)
    private String rollebeskrivelse;
    @ApiModelProperty(dataType = "java.lang.String", example = "Navn")
    private String fornavn;
    @ApiModelProperty(dataType = "java.lang.String", example = "Navnesen")
    private String slektsnavn;
    @ApiModelProperty(dataType = "java.lang.String", example = "Dollyveien 1")
    private String adresse1;
    @ApiModelProperty(dataType = "java.lang.String", example = "0576")
    private String postnr;
    @ApiModelProperty(dataType = "java.lang.String", example = "Oslo")
    private String poststed;
    @ApiModelProperty(example = "false", required = true)
    private boolean fratraadt;
}
