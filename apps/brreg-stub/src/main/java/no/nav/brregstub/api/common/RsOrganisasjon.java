package no.nav.brregstub.api.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import no.nav.brregstub.api.common.RsSamendring;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsOrganisasjon {

    @ApiModelProperty(example = "998877665", required = true)
    @NotNull
    private Integer orgnr;
    @ApiModelProperty(example = "0", required = true)
    private Integer hovedstatus = 0;
    private List<Integer> understatuser = new LinkedList<>();
    @ApiModelProperty(dataType = "java.lang.String", example = "1976-01-01", required = true)
    @NotNull
    private LocalDate registreringsdato;
    @Valid private RsSamendring kontaktperson;
    @Valid private RsSamendring sameier;
    @Valid private RsSamendring styre;
    @Valid private RsSamendring komplementar;
    @Valid private RsSamendring deltakere;
}
