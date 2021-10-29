package no.nav.dolly.domain.resultset.tpsf;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsBarnRelasjonRequest {

    public enum BorHos {MEG, OSS, DEG, BEGGE}

    @Schema(required = true,
            description = "Ident for barnet")
    private String ident;

    @Schema(description = "Ident som identifiserer partner for felles eller dine barn. Kan være tom for mine barn eller når det finnes kun en partner")
    private String partnerIdent;

    @Schema(description = "Barns boadresse bestemmes ut fra attributtverdi, og blank, MEG og OSS gir boadresse identisk med hovedperson, BEGGE gir delt bostedsadresse")
    private BorHos borHos;
}