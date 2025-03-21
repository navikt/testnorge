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
public class Aap115Response extends ArenaResponse{

    @Builder
    public Aap115Response(HttpStatus status, String miljoe, String feilmelding, List<Aap115> nyeAap115, List<Aap115Feil> nyeAapFeilList) {

        super(status, miljoe, feilmelding);
        this.nyeAap115 = nyeAap115;
        this.nyeAap115FeilList = nyeAapFeilList;
    }

    @Schema(description = "Liste over opprettede AAP-115")
    private List<Aap115> nyeAap115;

    @Schema(description = "Liste over AAP-115 hvor oppretting feilet")
    private List<Aap115Feil> nyeAap115FeilList;

    public static Flux<Aap115Response> of(WebClientError.Description description, String miljoe) {
        return Flux.just(Aap115Response
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .miljoe(miljoe)
                .build());
    }

    public List<Aap115> getNyeAap115() {

        if (isNull(nyeAap115)) {
            nyeAap115 = new ArrayList<>();
        }
        return nyeAap115;
    }

    public List<Aap115Feil> getNyeAap115FeilList() {

        if (isNull(nyeAap115FeilList)) {
            nyeAap115FeilList = new ArrayList<>();
        }
        return nyeAap115FeilList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Aap115Feil {

        private String personident;
        private String miljoe;
        private String nyAap115Feilstatus;
        private String melding;
    }
}
