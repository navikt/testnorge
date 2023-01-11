package no.nav.dolly.bestilling.pensjonforvalter.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlderspensjonRequest {

    private List<String> miljoer;

    private String pid;
    private LocalDate iverksettelsesdato;
    private Integer uttaksgrad;
    private String statsborgerskap;
    private String sivilstand;
    private LocalDate sivilstatusDatoFom;

    private List<AlderspensjonRequest.SkjemaRelasjon> relasjonListe;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkjemaRelasjon {
        @Schema(required = true)
        private LocalDate samboerFraDato;

        private LocalDate dodsdato;
        private Boolean varigAdskilt;
        private String fnr;
        private LocalDate samlivsbruddDato;
        private Boolean harVaertGift;
        private Boolean harFellesBarn;
        private Integer sumAvForvArbKapPenInntekt;
        private String relasjonType;
    }
}
