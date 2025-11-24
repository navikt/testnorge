package no.nav.dolly.bestilling.arenaforvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class AapResponse extends ArenaResponse {

    @Builder
    public AapResponse(HttpStatus status, String miljoe, String feilmelding, List<Aap> nyeAap, List<NyAapFeilV1> nyeAapFeilList) {
        super(status, miljoe, feilmelding);
        this.nyeAap = nyeAap;
        this.nyeAapFeilList = nyeAapFeilList;
    }

    @Schema(description = "AAP-rettigheter for brukeren")
    private List<Aap> nyeAap;

    @Schema(description = "liste over AAP hvor oppretting feilet")
    private List<NyAapFeilV1> nyeAapFeilList;

    public static Flux<AapResponse> of(WebClientError.Description description, String miljoe) {
        return Flux.just(AapResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .miljoe(miljoe)
                .build());
    }

    public List<Aap> getNyeAap() {

        if (isNull(nyeAap)) {
            nyeAap = new ArrayList<>();
        }
        return nyeAap;
    }

    public List<NyAapFeilV1> getNyeAapFeilList() {

        if (isNull(nyeAapFeilList)) {
            nyeAapFeilList = new ArrayList<>();
        }
        return nyeAapFeilList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyAapFeilV1 {

        private String personident;
        private String miljoe;
        private String nyAapFeilstatus;
        private String melding;
    }
}
