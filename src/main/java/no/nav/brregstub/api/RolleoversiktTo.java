package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RolleoversiktTo {

    @ApiModelProperty(dataType = "java.lang.String", example = "010176100000", required = true)
    @NotBlank private String fnr;

    @ApiModelProperty(dataType = "java.lang.String", example = "1976-01-01", required = true)
    private LocalDate fodselsdato;

    @NotNull
    @Valid
    private NavnTo navn;

    @NotNull
    @Valid
    private AdresseTo adresse;

    @Valid
    @NotEmpty
    private List<RolleTo> enheter = new LinkedList<>();

    @ApiModelProperty(example = "0", required = true)
    private Integer hovedstatus = 0;

    private List<Integer> understatuser = new LinkedList<>();
}
