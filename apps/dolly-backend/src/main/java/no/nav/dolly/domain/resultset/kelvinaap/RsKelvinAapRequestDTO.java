package no.nav.dolly.domain.resultset.kelvinaap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
        private List<Stoenad> stoenad;
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
        INTRODUKSJONSSTOENAD,
        KVALIFISERINGSSTOENAD,
        LAN,
        NEI,
        OMSORGSSTOENAD,
        STIPEND,
        UTLAND,
        VERV,
        OEKONOMISK_SOSIALHJELP
    }
}
