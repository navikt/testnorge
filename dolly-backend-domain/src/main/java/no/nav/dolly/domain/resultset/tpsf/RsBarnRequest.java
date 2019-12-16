package no.nav.dolly.domain.resultset.tpsf;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsBarnRequest extends RsRelasjon{

    public enum BarnType {MITT, FELLES, DITT}
    public enum BorHos {MEG, OSS, DEG}

    @ApiModelProperty(
            position = 1,
            value= "Bestemmer type av relasjon med forelder, enten FOEDSEL eller BARN"
    )
    private BarnType barnType;

    @ApiModelProperty(
            position = 2,
            value= "Identifiserer partner for felles barn. Kan være tom hvis felles eller mine, ellers er gyldige verdier er 1, 2 ... N"
    )
    private Integer partnerNr;

    @ApiModelProperty(
            position = 3,
            value= "Barns boadresse bestemmes ut fra attributtverdi, og blank, MEG og OSS gir boadresse identisk med hovedperson"
    )
    private BorHos borHos;

    @ApiModelProperty(
            position = 4,
            value= "Når barn er adoptert vil kun relasjon BARN benyttes for aktuelle foreldere"
    )
    private Boolean erAdoptert;
}