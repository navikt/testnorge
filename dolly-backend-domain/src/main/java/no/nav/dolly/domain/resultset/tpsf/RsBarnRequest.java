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
public class RsBarnRequest extends RsRelasjon {

    public enum BarnType {MITT, FELLES, DITT}

    public enum BorHos {MEG, OSS, DEG, BEGGE}

    @Schema(description = "Bestemmer type av relasjon med forelder, enten FOEDSEL eller BARN")
    private BarnType barnType;

    @Schema(description = "Identifiserer partner for felles barn. Kan være tom hvis felles eller mine, ellers er gyldige verdier er 1, 2 ... N")
    private Integer partnerNr;

    @Schema(description = "Barns boadresse bestemmes ut fra attributtverdi, og blank, MEG og OSS gir boadresse identisk med hovedperson, BEGGE gir delt bosted")
    private BorHos borHos;

    @Schema(description = "Når barn er adoptert vil kun relasjon BARN benyttes for aktuelle foreldere")
    private Boolean erAdoptert;
}