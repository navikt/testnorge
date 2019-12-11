package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
            dataType = "ZonedDateTime",
            value = "Registreringsdato på personopplysningene. Hvis blankt settes dagens dato."
    )
    private ZonedDateTime regdato;

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
            value = "Når satt vil personen ikke få boadresse. Må kompletteres med kommunenummer i boadresse.\n"
                    + "Feltet benyttes kun når UFB kommer i tillegg til annen diskresjonskode (spesreg)"
    )
    private Boolean utenFastBopel;

    @ApiModelProperty(
            position = 16,
            value = "Diskresjonskoder i hht. kodeverk 'Diskresjonskoder'"
    )
    private String spesreg;

    @ApiModelProperty(
            position = 17,
            dataType = "ZonedDateTime",
            value = "Diskresjonsdato, hvis blankt settes verdi lik fødselsdato"
    )
    private ZonedDateTime spesregDato;

    @ApiModelProperty(
            position = 18,
            value = "Når satt får personstatus verdien FOSV (forsvunnet/savnet)"
    )
    private Boolean erForsvunnet;

    @ApiModelProperty(
            position = 19,
            dataType = "ZonedDateTime",
            value = "Forsvunnet dato, hvis blankt settes verdi lik fødselsdato"
    )
    private ZonedDateTime forsvunnetDato;

    @ApiModelProperty(
            position = 20,
            dataType = "ZonedDateTime",
            value = "Egenansatt dato fra"
    )
    private ZonedDateTime egenAnsattDatoFom;

    @ApiModelProperty(
            position = 21,
            dataType = "ZonedDateTime",
            value = "Egenansatt dato til. Hvis tomt og egeneansatt-dato-fra er satt indikerer dette et nåværende NAV-ansatt forhold"
    )
    private ZonedDateTime egenAnsattDatoTom;

    @ApiModelProperty(
            position = 22,
            value = "Statsborgerskap. Hvis blankt velges Norge for FNR person og et tilfeldig annet land for DNR/BOST-person"
    )
    private String statsborgerskap;

    @ApiModelProperty(
            position = 23,
            dataType = "ZonedDateTime",
            value = "Statsborgerskap registeringsdato. Hvis blankt settes dato lik fødselsdato"
    )
    private ZonedDateTime statsborgerskapRegdato;

    @ApiModelProperty(
            position = 24,
            value = "Målform og språk i hht kodevek 'Språk'. Hvis blankt settes Norsk bokmål til FNR-person og tilfeldig språk for DNR-person"
    )
    private String sprakKode;

    @ApiModelProperty(
            position = 25,
            dataType = "ZonedDateTime",
            value = "Dato språk. Hvis tomt settes dato lik fødselsdato"
    )
    private ZonedDateTime datoSprak;

    @ApiModelProperty(
            position = 28,
            value = "Innvandret fra land i hht kodeverk 'Landkoder'. Hvis tomt settes et tilfeldig land"
    )
    private String innvandretFraLand;

    @ApiModelProperty(
            position = 29,
            dataType = "ZonedDateTime",
            value = "Innvandret fra land flyttedato. Hvis blankt settes dato lik fødselsdato"
    )
    private ZonedDateTime innvandretFraLandFlyttedato;

    @ApiModelProperty(
            position = 30,
            value = "Utvandret til land i hht kodeverk 'Landkoder'"
    )
    private String utvandretTilLand;

    @ApiModelProperty(
            position = 31,
            dataType = "ZonedDateTime",
            value = "Utvandret til land flyttedato. Hvis blankt og utvandret til land er satt benyttes dagens dato"
    )
    private ZonedDateTime utvandretTilLandFlyttedato;

    @ApiModelProperty(
            position = 32,
            dataType = "ZonedDateTime",
            value = "Sette dødsdato på person"
    )
    private LocalDateTime doedsdato;
}
