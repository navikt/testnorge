package no.nav.dolly.domain.resultset.tpsf.adresse;

import com.fasterxml.jackson.annotation.JsonTypeName;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("Gateadresse")
@NoArgsConstructor
@AllArgsConstructor
public class RsGateadresse extends RsAdresse {

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Gatenavn på adresse"
    )
    private String gateadresse;

    @ApiModelProperty(
            position = 2,
            value = "Husnummer på adresse"
    )
    private String husnummer;

    @ApiModelProperty(
            position = 3,
            required = true,
            value = "Gatekode fra adressesøket"
    )
    private String gatekode;

}
