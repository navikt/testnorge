package no.nav.dolly.domain.resultset.henvendelse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsHenvendelse {

    @Builder.Default
    private List<Melding> meldinger = new ArrayList<>();
    @Builder.Default
    private List<Samtalereferat> samtalereferater = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Melding {

        @Schema(description = "Verdi fra /henvendelse/kodeverk/temagrupper")
        private String temagruppe;
        @Schema(description = "Verdi fra sentralt kodeverk tema")
        private String tema;
        @Schema(description = "Verdi fra norg2-enhet; hvis tom setter backend denne basert på personens adresse")
        private String enhet;
        @Schema(description = "Meldingstekst")
        private String fritekst;
        @Schema(description = "Indikerer om meldingen skal tildeles meg")
        private Boolean tildelMeg;

        @Schema(description = "Referanse når dette er fortsettelse av en tidligere melding")
        private String kjedeId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Samtalereferat {

        @Schema(description = "Verdi fra /henvendelse/kodeverk/temagrupper")
        private String temagruppe;
        @Schema(description = "Verdi fra sentralt kodeverk tema")
        private String tema;
        @Schema(description = "Verdi fra norg2-enhet; hvis tom setter backend denne basert på personens adresse")
        private String enhet;
        @Schema(description = "Meldingstekst")
        private String fritekst;

        @Schema(description = "Referanse når dette er fortsettelse av en tidligere melding")
        private String kjedeId;
    }
}
