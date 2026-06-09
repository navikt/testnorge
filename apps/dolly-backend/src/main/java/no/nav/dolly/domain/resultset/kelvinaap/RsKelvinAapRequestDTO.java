package no.nav.dolly.domain.resultset.kelvinaap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private LocalDate soeknadsdato;
    private Boolean automatiskMeldekort;

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
        JA,
        NEI
    }

    public enum Stoenad {
        AFP,
        INTRODUKSJONSSTOENAD,
        KVALIFISERINGSSTOENAD,
        LAAN,
        NEI,
        OMSORGSSTOENAD,
        STIPEND,
        UTLAND,
        VERV,
        OEKONOMISK_SOSIALHJELP
    }
}
