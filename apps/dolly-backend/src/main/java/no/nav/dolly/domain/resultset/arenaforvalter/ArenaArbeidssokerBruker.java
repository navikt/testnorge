package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaArbeidssokerBruker {

    private HttpStatus status;
    private String feilmelding;
    private List<Arbeidssoker> arbeidsokerList;
    private Long antallSider;

    public List<Arbeidssoker> getArbeidsokerList() {

        if (isNull(arbeidsokerList)) {
            arbeidsokerList = new ArrayList<>();
        }
        return arbeidsokerList;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidssoker {

        private String personident;
        private String miljoe;
        private String status;
        private String eier;
        private String servicebehov;
        private String automatiskInnsendingAvMeldekort;
    }
}
