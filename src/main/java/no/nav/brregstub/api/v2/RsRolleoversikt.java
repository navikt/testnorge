package no.nav.brregstub.api.v2;

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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RsRolleoversikt {

    @ApiModelProperty(dataType = "java.lang.String", example = "010176100000", required = true)
    @NotBlank private String fnr;

    @ApiModelProperty(dataType = "java.lang.String", example = "1976-01-01", required = true)
    private LocalDate fodselsdato;

    @NotNull
    @Valid
    private RsNavn navn;

    @NotNull
    @Valid
    private RsAdresse adresse;

    @Valid
    @NotEmpty
    private List<RsRolle> enheter = new LinkedList<>();

    @ApiModelProperty(example = "0", required = true)
    private Integer hovedstatus = 0;

    private List<Integer> understatuser = new LinkedList<>();

    public List<Integer> getUnderstatuser() {
        if (understatuser == null) {
            understatuser = new ArrayList<>();
        }
        return understatuser;
    }
}
