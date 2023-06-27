package no.nav.dolly.bestilling.arenaforvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AapResponse {

    private HttpStatus status;
    private String feilmelding;
    private String miljoe;

    @Schema(description = "AAP-rettigheter for brukeren")
    private List<Aap> nyeAap;

    @Schema(description = "liste over AAP hvor oppretting feilet")
    private List<NyAapFeilV1> nyeAapFeilList;

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
