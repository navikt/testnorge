package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ArenaInnsatsbehovResponse extends ArenaResponse {

    public enum FeilStatus {DUPLIKAT, MILJOE_IKKE_STOETTET, ENDRE_INNSATSBEHOV}

    @Builder
    public ArenaInnsatsbehovResponse(HttpStatus status, String miljoe, String feilmelding, List<NyEndreInnsatsbehovFeil1> nyeEndreInnsatsbehovFeilList) {
        super(status, miljoe, feilmelding);
        this.nyeEndreInnsatsbehovFeilList = nyeEndreInnsatsbehovFeilList;
    }

    private List<NyEndreInnsatsbehovFeil1> nyeEndreInnsatsbehovFeilList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyEndreInnsatsbehovFeil1 {

        private String personident;
        private String miljoe;
        private FeilStatus nyEndreInnsatsbehovFeilstatus;
        private String melding;
    }
}
