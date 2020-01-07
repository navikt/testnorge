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
public class RsBarnRelasjonRequest {

    public enum BarnType {MITT, FELLES, DITT}
    public enum BorHos {MEG, OSS, DEG}

    @ApiModelProperty(
            position = 1,
            required = true,
            value= "Ident for barnet"
    )
    private String ident;

    @ApiModelProperty(
            position = 2,
            value= "Bestemmer type av relasjon med forelder"
    )
    private BarnType barnType;

    @ApiModelProperty(
            position = 3,
            value= "Ident som identifiserer partner for felles eller dine barn. Kan være tom for mine barn"
    )
    private String partnerIdent;

    @ApiModelProperty(
            position = 4,
            value= "Barns boadresse bestemmes ut fra attributtverdi, og blank, MEG og OSS gir boadresse identisk med hovedperson"
    )
    private BorHos borHos;

    @ApiModelProperty(
            position = 5,
            value= "Når barn er adoptert vil kun relasjon BARN (og ikke fødsel) benyttes for aktuelle foreldere"
    )
    private Boolean erAdoptert;
}