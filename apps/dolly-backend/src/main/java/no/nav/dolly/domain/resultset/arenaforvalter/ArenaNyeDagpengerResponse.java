package no.nav.dolly.domain.resultset.arenaforvalter;

import lombok.*;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class ArenaNyeDagpengerResponse extends ArenaResponse {

    @Builder
    public ArenaNyeDagpengerResponse(HttpStatus status, String miljoe, String feilmelding, List<Dagp> nyeDagp, List<NyeDagpResponse> nyeDagpResponse, List<NyDagpFeilV1> nyeDagpFeilList) {
        super(status, miljoe, feilmelding);
        this.nyeDagp = nyeDagp;
        this.nyeDagpResponse = nyeDagpResponse;
        this.nyeDagpFeilList = nyeDagpFeilList;
    }

    public static Flux<ArenaNyeDagpengerResponse> of(WebClientError.Description description, String miljoe) {
        return Flux.just(ArenaNyeDagpengerResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .miljoe(miljoe)
                .build());
    }

    public enum DagpFeilstatus {DUPLIKAT, MILJOE_IKKE_STOETTET, AKTIVER_DAGP}
    private List<Dagp> nyeDagp;
    private List<NyeDagpResponse> nyeDagpResponse;
    private List<NyDagpFeilV1> nyeDagpFeilList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dagp {

        private List<Vilkaar> vilkaar;
        private NyeDagpResponse nyeDagpResponse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyeDagpResponse {

        private String utfall;
        private String begrunnelse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vilkaar {

        private String kode;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyDagpFeilV1 {

        private String personident;
        private String miljoe;
        private DagpFeilstatus nyDagpFeilstatus;
        private String melding;
    }

    public List<Dagp> getNyeDagp() {
        if (isNull(nyeDagp)) {
            nyeDagp = new ArrayList<>();
        }
        return nyeDagp;
    }

    public List<NyeDagpResponse> getNyeDagpResponse() {
        if (isNull(nyeDagpResponse)) {
            nyeDagpResponse = new ArrayList<>();
        }
        return nyeDagpResponse;
    }

    public List<NyDagpFeilV1> getNyeDagpFeilList() {
        if (isNull(nyeDagpFeilList)) {
            nyeDagpFeilList = new ArrayList<>();
        }
        return nyeDagpFeilList;
    }
}
