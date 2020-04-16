package no.nav.dolly.domain.resultset.aareg;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsAktoerPerson extends RsAktoer {

    @ApiModelProperty(
            value = "Personident/fødselsnummer",
            required = true,
            position = 1
    )
    private String ident;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverk 'Personidenter'",
            required = true,
            position = 2
    )
    private String identtype;

    @Override
    public String getAktoertype() {
        return "PERS";
    }
}
