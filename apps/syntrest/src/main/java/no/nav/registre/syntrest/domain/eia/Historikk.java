package no.nav.registre.syntrest.domain.eia;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Historikk {

    @JsonProperty
    @ApiModelProperty("Endringshistorikken generert av char-RNN")
    private String endringshistorikk;

    @JsonProperty
    @ApiModelProperty("Sykemeldinger basert på endringshistorikken")
    private List<Sykemelding> sykemeldinger;
}
