package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaInnsatsbehov {

    private String personident;
    private String miljoe;
    private List<EndreInnsatsbehov> nyeEndreInnsatsbehov;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndreInnsatsbehov {

        private ArenaKvalifiseringsgruppe kvalifiseringsgruppe;
    }
}
