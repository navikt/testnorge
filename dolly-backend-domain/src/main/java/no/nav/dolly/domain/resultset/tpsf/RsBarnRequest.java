package no.nav.dolly.domain.resultset.tpsf;

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

    private BarnType barnType;
    private Integer partnerNr; // 1, 2, 3 Kan være tom hvis felles eller mine, identifiser partner hvis dine
    private BorHos borHos;
    private Boolean erAdoptert;
}