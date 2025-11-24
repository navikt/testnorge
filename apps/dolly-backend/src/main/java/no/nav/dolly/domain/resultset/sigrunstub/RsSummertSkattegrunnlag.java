package no.nav.dolly.domain.resultset.sigrunstub;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsSummertSkattegrunnlag {

    private LocalDateTime ajourholdstidspunkt;
    private List<Grunnlag> grunnlag;
    private String inntektsaar;
    private List<Grunnlag> kildeskattPaaLoennGrunnlag;
    private LocalDate skatteoppgjoersdato;
    private Boolean skjermet;
    @Schema(description = "verdi fra kodeverk \"Skattegrunnlag stadie\"")
    private String stadie;
    private List<Grunnlag> svalbardGrunnlag;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Grunnlag {

        private Integer andelOverfoertFraBarn;
        private Integer beloep;
        @Schema(description = "verdi fra kodeverk \"Summert skattegrunnlag - kategori\"")
        private String kategori;
        private List<Kjoeretoey> spesifisering;
        @Schema(description = "verdi fra kodeverk \"Summert skattegrunnlag\"")
        private String tekniskNavn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kjoeretoey {

        private Type type;
        private String aarForFoerstegangsregistrering;
        private Integer antattMarkedsverdi;
        private Integer antattVerdiSomNytt;
        private Integer beloep;
        private Double eierandel;
        private String fabrikatnavn;
        private Integer formuesverdi;
        private Integer formuesverdiForFormuesandel;
        private String registreringsnummer;
    }

    public enum Type {
        EiendelerOgFasteEiendommer,
        Kjoeretoey,
        Spesifisering
    }
}
