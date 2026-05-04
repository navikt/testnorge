package no.nav.dolly.bestilling.kelvinaap.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AapOpprettRequest {

    private AndreUtbetalingerDTO andreUtbetalinger;

    private Boolean erStudent;
    private Boolean harMedlemskap;
    private Boolean harYrkesskade;
    private String ident;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AndreUtbetalingerDTO {

        private AfpDTO afp;
        private Loenn loenn;
        private Stoenad stoenad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AfpDTO {

        private String hvemBetaler;
    }

    public enum Loenn {
        Ja,
        Nei
    }

    public enum Stoenad {
        AFP,
        INTRODUKSJONSSTØNAD,
        KVALIFISERINGSSTØNAD,
        LÅN,
        NEI,
        OMSORGSSTØNAD,
        STIPEND,
        UTLAND,
        VERV,
        ØKONOMISK_SOSIALHJELP
    }
}