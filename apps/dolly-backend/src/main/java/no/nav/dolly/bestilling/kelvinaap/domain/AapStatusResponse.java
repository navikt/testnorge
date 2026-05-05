package no.nav.dolly.bestilling.kelvinaap.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AapStatusResponse {

    private String saksnummer;
    private String behandlingStatus;
    private Boolean ferdig;
    private Soeknad soeknad;

    private HttpStatus status;
    private String error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Soeknad {

        private Boolean erStudent;
        private Boolean harYrkesskade;
        private Boolean harMedlemskap;

        private AndreUtbetalingerDTO andreUtbetalinger;
        private Loenn loenn;
        private AfpDTO afp;
        private Stoenad stoenad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AndreUtbetalingerDTO {

        private Loenn loenn;
        private AfpDTO afp;
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