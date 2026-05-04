package no.nav.dolly.domain.resultset.kelvinaap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.bestilling.kelvinaap.domain.AapOpprettRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsKelvinAapRequestDTO {

    private AndreUtbetalingerDTO andreUtbetalinger;

    private Boolean erStudent;
    private Boolean harMedlemskap;
    private Boolean harYrkesskade;

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
