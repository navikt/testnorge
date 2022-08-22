package no.nav.dolly.domain.resultset.tpsf.adresse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsMidlertidigAdresse {

    @Schema(description = "Midlertidig adresse gyldig til-og-med. Default er et år frem i tid. "
            + "Dato kan ikke velges før dagens dato, ei heller lengre frem i tid enn et år fra dagens dato")
    private LocalDateTime gyldigTom;
    @Schema(description = "Adressetype GATE (default), samt MATR og PBOX innebærer midlertidig norsk adresse, "
            + "UTAD er utenlandsk tilleggsadresse")
    private Adressetype adressetype;
    @Schema(description = "Benyttes ved GATE. Backendgenerert adresse basert på postnummer eller kommunenr")
    private AdresseNrInfo gateadresseNrInfo;
    @Schema(description = "Benyttes for norsk adresse, GATE, STED eller PBOX")
    private NorskAdresse norskAdresse;
    @Schema(description = "Benyttes ved UTAD. Postland må være annet enn \"NOR\"")
    private RsPostadresse utenlandskAdresse;

    public enum Adressetype {PBOX, GATE, STED, UTAD}

    public enum TilleggType {CO_NAVN, BOLIG_NR}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NorskAdresse {

        @Schema(description = "Benyttes sammen med GATE eller STED-adresse")
        private MidlertidigTilleggAdresse tilleggsadresse;

        @Schema(description = "Relevant for alle adressetyper. "
                + "Dropdown for GATE + STED bør vise postnummer for vegadresse. "
                + "Dropdown for BPOX bør vise postboksadresser kun",
                required = true)
        private String postnr;

        @Schema(description = "Relevant for GATE-adresse")
        private String gatenavn;

        @Schema(description = "Må fylles ut for GATE-adresse")
        private String gatekode;

        @Schema(description = "Relevant for GATE-adresse")
        private String husnr;

        @Schema(description = "Relevant for GATE-adresse")
        private String matrikkelId;

        @Schema(description = "Må fylles ut for STED-adresse")
        private String eiendomsnavn;

        @Schema(description = "Må fylles ut for PBOX-adresse")
        private String postboksnr;

        @Schema(description = "Relevant for PBOX-adresse")
        private String postboksAnlegg;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MidlertidigTilleggAdresse {

        @Schema(description = "Tilleggstype, ved CO_NAVN benyttes ikke \"nummer\" feltet")
        private TilleggType tilleggType;

        @Schema(description = "Nummer for bolig")
        private Integer nummer;
    }
}