package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RolleTo {
    @ApiModelProperty(dataType = "java.lang.String", example = "2020-01-01")
    private LocalDate registreringsdato;
    @ApiModelProperty(example = "Innehaver", required = true)
    private String rollebeskrivelse;
    @ApiModelProperty(example = "998877665", required = true)
    private Integer orgNr;
    private NavnTo foretaksNavn;
    private AdresseTo forretningsAdresse;
    private AdresseTo postAdresse;
}
