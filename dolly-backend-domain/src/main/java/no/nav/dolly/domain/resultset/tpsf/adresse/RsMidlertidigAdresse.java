package no.nav.dolly.domain.resultset.tpsf.adresse;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsMidlertidigAdresse {

    public enum Adressetype {PBOX, GATE, STED, UTAD}

    public enum TilleggType {CO_NAVN, BOLIG_NR}

    @ApiModelProperty(
            position = 2,
            value = "Midlertidig adresse gyldig til-og-med. Default er et år frem i tid. "
                    + "Dato kan ikke velges før dagens dato, ei heller lengre frem i tid enn et år fra dagens dato"
    )
    private LocalDateTime gyldigTom;

    @ApiModelProperty(
            position = 3,
            value = "Adressetype GATE (default), samt MATR og PBOX innebærer midlertidig norsk adresse, "
                    + "UTAD er utenlandsk tilleggsadresse"
    )
    private Adressetype adressetype;

    @ApiModelProperty(
            position = 4,
            value = "Benyttes ved GATE. Backendgenerert adresse basert på postnummer eller kommunenr"
    )
    private AdresseNrInfo gateadresseNrInfo;

    @ApiModelProperty(
            position = 5,
            value = "Benyttes for norsk adresse, GATE, STED eller PBOX"
    )
    private NorskAdresse norskAdresse;

    @ApiModelProperty(
            position = 6,
            value = "Benyttes ved UTAD. Postland må være annet enn \"NOR\""
    )
    private RsPostadresse utenlandskAdresse;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NorskAdresse {

        @ApiModelProperty(
                position = 1,
                value = "Benyttes sammen med GATE eller STED-adresse"
        )
        private MidlertidigTilleggAdresse tilleggsadresse;

        @ApiModelProperty(
                position = 3,
                value = "Relevant for alle adressetyper. "
                        + "Dropdown for GATE + STED bør vise postnummer for vegadresse. "
                        + "Dropdown for BPOX bør vise postboksadresser kun",
                required = true
        )
        private String postnr;

        @ApiModelProperty(
                position = 4,
                value = "Relevant for GATE-adresse"
        )
        private String gatenavn;

        @ApiModelProperty(
                position = 5,
                value = "Må fylles ut for GATE-adresse"
        )
        private String gatekode;

        @ApiModelProperty(
                position = 6,
                value = "Relevant for GATE-adresse"
        )
        private String husnr;

        @ApiModelProperty(
                position = 7,
                value = "Må fylles ut for STED-adresse"
        )
        private String eiendomsnavn;

        @ApiModelProperty(
                position = 9,
                value = "Må fylles ut for PBOX-adresse"
        )
        private String postboksnr;

        @ApiModelProperty(
                position = 10,
                value = "Relevant for PBOX-adresse"
        )
        private String postboksAnlegg;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MidlertidigTilleggAdresse {

        @ApiModelProperty(
                position = 1,
                value = "Tilleggstype, ved CO_NAVN benyttes ikke \"nummer\" feltet"
        )
        private TilleggType tilleggType;

        @ApiModelProperty(
                position = 2,
                value = "Nummer for bolig"
        )
        private Integer nummer;
    }
}