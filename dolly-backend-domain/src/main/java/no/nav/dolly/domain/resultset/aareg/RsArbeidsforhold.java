package no.nav.dolly.domain.resultset.aareg;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RsArbeidsforhold {

    @ApiModelProperty(
            required = true,
            position = 3
    )
    private RsPeriode ansettelsesPeriode;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverk 'Arbeidsforholdstyper'",
            required = true,
            position = 4
    )
    private String arbeidsforholdstype;

    @ApiModelProperty(
            position = 5
    )
    private List<RsAntallTimerIPerioden> antallTimerForTimeloennet;

    @ApiModelProperty(
            required = true,
            position = 6
    )
    private RsArbeidsavtale arbeidsavtale;

    @ApiModelProperty(
            position = 7
    )
    private List<RsPermisjon> permisjon;

    @ApiModelProperty(
            position = 8
    )
    private List<RsUtenlandsopphold> utenlandsopphold;

    @ApiModelProperty(
            required = true,
            position = 9
    )
    private RsAktoer arbeidsgiver;
}
