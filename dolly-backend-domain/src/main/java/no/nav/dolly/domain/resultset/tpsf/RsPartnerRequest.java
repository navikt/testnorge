package no.nav.dolly.domain.resultset.tpsf;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsPartnerRequest extends RsRelasjon{

    @ApiModelProperty(
            position = 1,
            value = "Tagger partner og verdi matches mot tilsvarende atributt for barn. Gyldige verdier er 1, 2 ... N."
    )
    private Integer partnerNr;

    @ApiModelProperty(
            position = 2
    )
    private List<RsSivilstandRequest> sivilstander;

    @ApiModelProperty(
            position = 3,
            value = "Når true (eller blankt) får partner samme adresse som hovedperson. False innebærer ulik boadresse."
    )
    private Boolean harFellesAdresse;
}