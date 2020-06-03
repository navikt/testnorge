package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.adresse.AdresseNrInfo;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTpsfBasisBestilling {

    @ApiModelProperty(
            position = 10,
            dataType = "LocalDateTime",
            value = "Registreringsdato på personopplysningene. Hvis blankt settes dagens dato."
    )
    private LocalDateTime regdato;

    @ApiModelProperty(
            position = 11,
            value = "Når satt genereres mellomnavn på testpersonen"
    )
    private Boolean harMellomnavn;

    @ApiModelProperty(
            position = 12
    )
    private AdresseNrInfo adresseNrInfo;

    @ApiModelProperty(
            position = 13,
            value = "Merknad: Boadresse er enten gateadresse (GATE) eller matrikkeladresse (MATR)"
    )
    private RsAdresse boadresse;

    @ApiModelProperty(
            position = 14,
            value = "Merknad: Foreløbig benyttes kun første postadresse i tabell"
    )
    private List<RsPostadresse> postadresse;

    @ApiModelProperty(
            position = 15,
            value = "Merknad: Foreløbig benyttes kun første postadresse i tabell"
    )
    private RsMidlertidigAdresse midlertidigAdresse;

    @ApiModelProperty(
            position = 16,
            value = "Når satt vil personen ikke få boadresse. Må kompletteres med kommunenummer i boadresse.\n"
                    + "Feltet benyttes kun når UFB kommer i tillegg til annen diskresjonskode (spesreg)"
    )
    private Boolean utenFastBopel;

    @ApiModelProperty(
            position = 17,
            value = "Diskresjonskoder i hht. kodeverk 'Diskresjonskoder'"
    )
    private String spesreg;

    @ApiModelProperty(
            position = 18,
            dataType = "LocalDateTime",
            value = "Diskresjonsdato, hvis blankt settes verdi lik fødselsdato"
    )
    private LocalDateTime spesregDato;

    @ApiModelProperty(
            position = 19,
            value = "Når satt får personstatus verdien FOSV (forsvunnet/savnet)"
    )
    private Boolean erForsvunnet;

    @ApiModelProperty(
            position = 20,
            dataType = "LocalDateTime",
            value = "Forsvunnet dato, hvis blankt settes verdi lik fødselsdato"
    )
    private LocalDateTime forsvunnetDato;

    @ApiModelProperty(
            position = 21,
            dataType = "LocalDateTime",
            value = "Egenansatt dato fra"
    )
    private LocalDateTime egenAnsattDatoFom;

    @ApiModelProperty(
            position = 22,
            dataType = "LocalDateTime",
            value = "Egenansatt dato til. Hvis tomt og egeneansatt-dato-fra er satt indikerer dette et nåværende NAV-ansatt forhold"
    )
    private LocalDateTime egenAnsattDatoTom;

    @ApiModelProperty(
            position = 23,
            value = "Statsborgerskap. Hvis blankt velges Norge for FNR person og et tilfeldig annet land for DNR/BOST-person"
    )
    private String statsborgerskap;

    @ApiModelProperty(
            position = 24,
            dataType = "LocalDateTime",
            value = "Statsborgerskap registeringsdato. Hvis blankt settes dato lik fødselsdato"
    )
    private LocalDateTime statsborgerskapRegdato;

    @ApiModelProperty(
            position = 25,
            value = "Målform og språk i hht kodevek 'Språk'. Hvis blankt settes Norsk bokmål til FNR-person og tilfeldig språk for DNR-person"
    )
    private String sprakKode;

    @ApiModelProperty(
            position = 26,
            dataType = "LocalDateTime",
            value = "Dato språk. Hvis tomt settes dato lik fødselsdato"
    )
    private LocalDateTime datoSprak;

    @ApiModelProperty(
            position = 27,
            value = "Innvandret fra land i hht kodeverk 'Landkoder'. Hvis tomt settes et tilfeldig land"
    )
    private String innvandretFraLand;

    @ApiModelProperty(
            position = 28,
            dataType = "LocalDateTime",
            value = "Innvandret fra land flyttedato. Hvis blankt settes dato lik fødselsdato"
    )
    private LocalDateTime innvandretFraLandFlyttedato;

    @ApiModelProperty(
            position = 29,
            value = "Utvandret til land i hht kodeverk 'Landkoder'"
    )
    private String utvandretTilLand;

    @ApiModelProperty(
            position = 30,
            dataType = "LocalDateTime",
            value = "Utvandret til land flyttedato. Hvis blankt og utvandret til land er satt benyttes dagens dato"
    )
    private LocalDateTime utvandretTilLandFlyttedato;

    @ApiModelProperty(
            position = 31,
            dataType = "LocalDateTime",
            value = "Sette dødsdato på person"
    )
    private LocalDateTime doedsdato;

    @ApiModelProperty(
            position = 32,
            dataType = "Boolean",
            value = "Når satt genereres et gyldig bankkontonummer som legges til på person-egenskaper"
    )
    private Boolean harBankkontonr;

    @ApiModelProperty(
            position = 33,
            dataType = "LocalDateTime",
            value = "Dato for opprettet bankkontonummer"
    )
    private LocalDateTime bankkontonrRegdato;

    @ApiModelProperty(
            position = 34,
            dataType = "String",
            value = "Landskode 1 fra kodeverk 'Retningsnumre'"
    )
    private String telefonLandskode_1;

    @ApiModelProperty(
            position = 35,
            dataType = "String",
            value = "Telefonnummer 1, maxs 20 sifre"
    )
    private String telefonnummer_1;

    @ApiModelProperty(
            position = 36,
            dataType = "String",
            value = "Landskode 2 fra kodeverk 'Retningsnumre'"
    )
    private String telefonLandskode_2;

    @ApiModelProperty(
            position = 37,
            dataType = "String",
            value = "Telefonnummer 2, maxs 20 sifre"
    )
    private String telefonnummer_2;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsMidlertidigAdresse {

        public enum Adressetype {GATE, MATR, UTAD}

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
}
