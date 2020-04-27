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
public class RolleutskriftTo {

    @ApiModelProperty(dataType = "java.lang.String", example = "010176100000", required = true)
    private String fnr;
    @ApiModelProperty(dataType = "java.lang.String", example = "1976-01-01", required = true)
    private LocalDate fodselsdato;
    private NavnTo navn;
    private AdresseTo adresse;
    private List<RolleTo> enheter;
    @ApiModelProperty(example = "0", required = true)
    private Integer hovedstatus;
    private List<Integer> understatuser = new LinkedList<>();
}
