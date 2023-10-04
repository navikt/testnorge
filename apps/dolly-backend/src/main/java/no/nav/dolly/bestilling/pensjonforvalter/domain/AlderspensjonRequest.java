package no.nav.dolly.bestilling.pensjonforvalter.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlderspensjonRequest {

    private List<String> miljoer;

    private String fnr;
    private LocalDate iverksettelsesdato;
    private Integer uttaksgrad;
    private String statsborgerskap;
    private String sivilstand;
    private LocalDate sivilstandDatoFom;

    private List<AlderspensjonRequest.SkjemaRelasjon> relasjonListe;

    public List<String> getMiljoer() {

        if (isNull(miljoer)) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }

    public List<SkjemaRelasjon> getRelasjonListe() {

        if (isNull(relasjonListe)) {
            relasjonListe = new ArrayList<>();
        }
        return relasjonListe;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkjemaRelasjon {

        @Schema
        private LocalDate relasjonFraDato;

        private LocalDate dodsdato;
        private Boolean varigAdskilt;
        private String fnr;
        private LocalDate samlivsbruddDato;
        private Boolean harVaertGift;
        private Boolean harFellesBarn;
        private Integer sumAvForventetArbeidKapitalPensjonInntekt;
        private String relasjonType;
    }
}
