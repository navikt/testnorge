package no.nav.brregstub.api.v1;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;

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

    private RsNavn foretaksNavn;

    private RsAdresse forretningsAdresse;

    private RsAdresse postAdresse;
}
