package no.nav.brregstub.api.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RsSamendring {

    @ApiModelProperty(dataType = "java.lang.String", example = "2004-01-01", required = true)
    @NotNull
    private LocalDate registringsDato;

    @NotEmpty
    @Valid
    private List<RsPersonOgRolle> roller = new LinkedList<>();

}
