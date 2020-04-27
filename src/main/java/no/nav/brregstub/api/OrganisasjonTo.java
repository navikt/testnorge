package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrganisasjonTo {

    @ApiModelProperty(example = "998877665", required = true)
    private Integer orgnr;
    @ApiModelProperty(example = "0", required = true)
    private Integer hovedstatus;
    private List<Integer> understatuser = new LinkedList<>();
    @ApiModelProperty(dataType = "java.lang.String", example = "1976-01-01", required = true)
    private LocalDate registreringsdato;
    private SamendringTo kontaktperson;
    private SamendringTo sameier;
    private SamendringTo styre;
    private SamendringTo komplementar;
    private SamendringTo deltakere;
}
