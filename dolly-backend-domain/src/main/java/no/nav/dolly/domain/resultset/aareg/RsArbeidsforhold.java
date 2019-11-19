package no.nav.dolly.domain.resultset.aareg;

import static java.util.Objects.isNull;

import java.util.ArrayList;
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
            position = 1
    )
    private String arbeidsforholdID;

    @ApiModelProperty(
            position = 2
    )
    private Long arbeidsforholdIDnav;

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

    @ApiModelProperty(
            position = 10
    )
    private RsPersonAareg arbeidstaker;

    public List<RsAntallTimerIPerioden> getAntallTimerForTimeloennet() {
        if (isNull(antallTimerForTimeloennet)) {
            antallTimerForTimeloennet = new ArrayList<>();
        }
        return antallTimerForTimeloennet;
    }

    public List<RsUtenlandsopphold> getUtenlandsopphold() {
        if (isNull(utenlandsopphold)) {
            utenlandsopphold = new ArrayList<>();
        }
        return utenlandsopphold;
    }

    public List<RsPermisjon> getPermisjon() {
        if (isNull(permisjon)) {
            permisjon = new ArrayList<>();
        }
        return permisjon;
    }
}
