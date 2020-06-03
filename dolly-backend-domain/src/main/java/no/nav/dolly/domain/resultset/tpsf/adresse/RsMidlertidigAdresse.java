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

    public enum Adressetype {PBOX, GATE, MATR, UTAD}

    @ApiModelProperty(
            position = 1,
            value = "Midlertidig adresse gyldig fra-og-med. Default er dagens dato"
    )
    private LocalDateTime gyldigFom;

    @ApiModelProperty(
            position = 2,
            value = "Midlertidig adresse gyldig til-og-med. Default er et år frem i tid"
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
            value = "Benyttes når midlertidig adresse enten er gateadresse (GATE) eller matrikkeladresse (MATR). "
                    + "Flyttedato benyttes ikke."
    )
    private RsAdresse norskAdresse;

    @ApiModelProperty(
            position = 6,
            value = "Benyttes ved UTAD. Postland må være annet enn \"NOR\""
    )
    private RsPostadresse utenlandskAdresse;
}