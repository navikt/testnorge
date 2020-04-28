package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RolleTo {

    @ApiModelProperty(dataType = "java.lang.String", example = "2020-01-01")
    @NotNull
    private LocalDate registreringsdato;

    @ApiModelProperty(example = "Innehaver", required = true)
    @NotBlank
    private String rollebeskrivelse;

    @ApiModelProperty(example = "998877665", required = true)
    @NotNull
    private Integer orgNr;

    private NavnTo foretaksNavn;

    private AdresseTo forretningsAdresse;

    private AdresseTo postAdresse;
}
