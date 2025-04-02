package no.nav.dolly.bestilling.sigrunstub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigrunstubSummertskattegrunnlagRequest {

    private List<Summertskattegrunnlag> summertskattegrunnlag;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summertskattegrunnlag {

        private OffsetDateTime ajourholdstidspunkt;
        private List<Grunnlag> grunnlag;
        private String inntektsaar;
        private List<Grunnlag> kildeskattPaaLoennGrunnlag;
        private LocalDate skatteoppgjoersdato;
        private String personidentifikator;
        private Boolean skjermet;
        private String stadie;
        private List<Grunnlag> svalbardGrunnlag;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Grunnlag {

        private Integer andelOverfoertFraBarn;
        private Integer beloep;
        private String kategori;
        private List<Kjoeretoey> spesifisering;
        private String tekniskNavn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kjoeretoey {

        private String type;
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
}
