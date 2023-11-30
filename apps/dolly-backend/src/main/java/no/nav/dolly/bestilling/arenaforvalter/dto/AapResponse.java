package no.nav.dolly.bestilling.arenaforvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

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
