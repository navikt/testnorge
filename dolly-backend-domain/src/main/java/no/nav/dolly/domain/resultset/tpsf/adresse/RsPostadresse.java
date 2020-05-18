package no.nav.dolly.domain.resultset.tpsf.adresse;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonIgnore
    public boolean isNorsk() {
        return isNull(postLand) || "NOR".equals(postLand);
    }
}
