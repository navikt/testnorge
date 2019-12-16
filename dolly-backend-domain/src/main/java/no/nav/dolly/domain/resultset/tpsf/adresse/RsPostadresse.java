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
public class RsPostadresse {

    @ApiModelProperty(
            required = true,
            position = 1,
            value = "Adresselinje 1 fritekst postadresse eller boadresse"
    )
    private String postLinje1;

    @ApiModelProperty(
            position = 2,
            value = "Adresselinje 2 fritekst postadresse eller boadresse"
    )
    private String postLinje2;

    @ApiModelProperty(
            position = 3,
            value = "Adresselinje 3 fritekst postadresse eller boadresse"
    )
    private String postLinje3;

    @ApiModelProperty(
            position = 4,
            value = "Landkode i hht kodeverk 'Landkoder'. Tomt felt eller NOR betyr Norge og siste benyttede adresselinje 1, 2 eller 3 må starte med postnummer"
    )
    private String postLand;

}
