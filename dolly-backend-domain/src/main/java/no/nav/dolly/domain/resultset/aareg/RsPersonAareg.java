package no.nav.dolly.domain.resultset.aareg;

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
public class RsPersonAareg {

    @ApiModelProperty(
            value = "Personident/fødselsnummer",
            position = 1
    )
    private String ident;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverk 'Personidenter'",
            position = 2
    )
    private String identtype;
}