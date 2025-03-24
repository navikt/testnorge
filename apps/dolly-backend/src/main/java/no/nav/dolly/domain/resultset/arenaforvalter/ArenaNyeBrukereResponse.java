package no.nav.dolly.domain.resultset.arenaforvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class ArenaNyeBrukereResponse extends ArenaResponse {

    private List<ArenaBruker> arbeidsokerList;
    private List<NyBrukerFeilV1> nyBrukerFeilList;

    @Builder
    public ArenaNyeBrukereResponse(HttpStatus status, String miljoe, String feilmelding, List<ArenaBruker> arbeidsokerList, List<NyBrukerFeilV1> nyBrukerFeilList) {
        super(status, miljoe, feilmelding);
        this.arbeidsokerList = arbeidsokerList;
        this.nyBrukerFeilList = nyBrukerFeilList;
    }

    public static Publisher<ArenaNyeBrukereResponse> of(WebClientError.Description description, String miljoe) {
        return Flux.just(ArenaNyeBrukereResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .miljoe(miljoe)
                .build());
    }

    public List<ArenaBruker> getArbeidsokerList() {
        if (isNull(arbeidsokerList)) {
            arbeidsokerList = new ArrayList<>();
        }
        return arbeidsokerList;
    }

    public List<NyBrukerFeilV1> getNyBrukerFeilList() {
        if (isNull(nyBrukerFeilList)) {
            nyBrukerFeilList = new ArrayList<>();
        }
        return nyBrukerFeilList;
    }

    public enum BrukerFeilstatus {
        DUPLIKAT, MILJOE_IKKE_STOETTET, FINNES_ALLEREDE_PAA_VALGT_MILJO,
        BRUKEREN_ER_IKKE_REGISTRERT, AKTIVER_BRUKER, INAKTIVER_BRUKER, AKTIVER_AAP_115, AKTIVER_AAP
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyBrukerFeilV1 {

        private String personident;
        private String miljoe;
        private BrukerFeilstatus nyBrukerFeilstatus;
        private String melding;
    }
}
