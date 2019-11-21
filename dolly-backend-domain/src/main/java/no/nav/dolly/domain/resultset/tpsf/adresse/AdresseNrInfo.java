package no.nav.dolly.domain.resultset.tpsf.adresse;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdresseNrInfo {

    public enum AdresseNr {KOMMUNENR, POSTNR}

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Angir om backend generert boadresse skal baseres på KOMMUNENR eller POSTNR. Default er random for hele landet."
    )
    private AdresseNr nummertype;

    @ApiModelProperty(
            position = 2,
            required = true,
            value = "Angir verdi for kommune- eller postnummer"
    )
    private String nummer;
}
