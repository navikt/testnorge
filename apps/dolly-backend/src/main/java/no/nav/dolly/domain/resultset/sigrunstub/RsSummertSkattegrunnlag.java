package no.nav.dolly.domain.resultset.sigrunstub;

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
    private List<KildeskattPaaLoennGrunnlag> kildeskattPaaLoennGrunnlag;
    private LocalDate skatteoppgjoersdato;
    private Boolean skjermet;
    private String stadie;
    private List<SvalbardGrunnlag> svalbardGrunnlag;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SvalbardGrunnlag {

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
    public static class KildeskattPaaLoennGrunnlag {

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
