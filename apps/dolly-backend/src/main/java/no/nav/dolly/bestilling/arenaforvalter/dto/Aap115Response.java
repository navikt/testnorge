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
public class Aap115Response {

    private HttpStatus status;
    private String feilmelding;
    private String miljoe;

    @Schema(description = "Liste over opprettede AAP-115")
    private List<Aap115> nyeAap115;

    @Schema(description = "Liste over AAP-115 hvor oppretting feilet")
    private List<Aap115Feil> nyeAapFeilList;

    public List<Aap115> getNyeAap115() {

        if (isNull(nyeAap115)) {
            nyeAap115 = new ArrayList<>();
        }
        return nyeAap115;
    }

    public List<Aap115Feil> getNyeAapFeilList() {

        if (isNull(nyeAapFeilList)) {
            nyeAapFeilList = new ArrayList<>();
        }
        return nyeAapFeilList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Aap115Feil {

        private String personident;
        private String miljoe;
        private String nyAapFeilstatus;
        private String melding;
    }
}
