package no.nav.dolly.domain.resultset.tpsf;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.adresse.AdresseNrInfo;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsMidlertidigAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTpsfBasisBestilling {

    @Schema(type = "LocalDateTime",
            description = "Registreringsdato på personopplysningene. Hvis blankt settes dagens dato.")
    private LocalDateTime regdato;

    @Schema(description = "Når satt genereres mellomnavn på testpersonen")
    private Boolean harMellomnavn;

    private AdresseNrInfo adresseNrInfo;

    @Schema(description = "Merknad: Boadresse er enten gateadresse (GATE) eller matrikkeladresse (MATR)")
    private RsAdresse boadresse;

    @Schema(description = "Merknad: Foreløbig benyttes kun første postadresse i tabell")
    private List<RsPostadresse> postadresse;

    @Schema(description = "Merknad: Foreløbig benyttes kun første postadresse i tabell")
    private RsMidlertidigAdresse midlertidigAdresse;

    @Schema(description = "Når satt vil personen ikke få boadresse. Må kompletteres med kommunenummer i boadresse.\n"
            + "Feltet benyttes kun når UFB kommer i tillegg til annen diskresjonskode (spesreg)")
    private Boolean utenFastBopel;

    @Schema(description = "Diskresjonskoder i hht. kodeverk 'Diskresjonskoder'")
    private String spesreg;

    @Schema(type = "LocalDateTime",
            description = "Diskresjonsdato, hvis blankt settes verdi lik fødselsdato")
    private LocalDateTime spesregDato;

    @Schema(description = "Når satt får personstatus verdien FOSV (forsvunnet/savnet)")
    private Boolean erForsvunnet;

    @Schema(type = "LocalDateTime",
            description = "Forsvunnet dato, hvis blankt settes verdi lik fødselsdato")
    private LocalDateTime forsvunnetDato;

    @Schema(type = "LocalDateTime",
            description = "Egenansatt dato fra")
    private LocalDateTime egenAnsattDatoFom;

    @Schema(type = "LocalDateTime",
            description = "Egenansatt dato til. Hvis tomt og egeneansatt-dato-fra er satt indikerer dette et nåværende NAV-ansatt forhold")
    private LocalDateTime egenAnsattDatoTom;

    @Schema(description = "Statsborgerskap. Hvis blankt velges Norge for FNR person og et tilfeldig annet land for DNR/BOST-person")
    private String statsborgerskap;

    @Schema(type = "LocalDateTime",
            description = "Statsborgerskap registeringsdato. Hvis blankt settes dato lik fødselsdato")
    private LocalDateTime statsborgerskapRegdato;

    @Schema(type = "LocalDateTime",
            description = "Statsborgerskap avslutningsdato.")
    private LocalDateTime statsborgerskapTildato;

    @Schema(description = "Målform og språk i hht kodevek 'Språk'. Hvis blankt settes Norsk bokmål til FNR-person og tilfeldig språk for DNR-person")
    private String sprakKode;

    @Schema(type = "LocalDateTime",
            description = "Dato språk. Hvis tomt settes dato lik fødselsdato")
    private LocalDateTime datoSprak;

    @Schema(description = "Innvandret fra land i hht kodeverk 'Landkoder'. Hvis tomt settes et tilfeldig land")
    private String innvandretFraLand;

    @Schema(type = "LocalDateTime",
            description = "Innvandret fra land flyttedato. Hvis blankt settes dato lik fødselsdato")
    private LocalDateTime innvandretFraLandFlyttedato;

    @Schema(description = "Utvandret til land i hht kodeverk 'Landkoder'")
    private String utvandretTilLand;

    @Schema(type = "LocalDateTime",
            description = "Utvandret til land flyttedato. Hvis blankt og utvandret til land er satt benyttes dagens dato")
    private LocalDateTime utvandretTilLandFlyttedato;

    @Schema(type = "LocalDateTime",
            description = "Sette dødsdato på person")
    private LocalDateTime doedsdato;

    @Schema(type = "Boolean",
            description = "Når satt genereres et gyldig bankkontonummer som legges til på person-egenskaper")
    private Boolean harBankkontonr;

    @Schema(type = "LocalDateTime",
            description = "Dato for opprettet bankkontonummer")
    private LocalDateTime bankkontonrRegdato;

    @Schema(type = "String",
            description = "Landskode 1 fra kodeverk 'Retningsnumre'")
    private String telefonLandskode_1;

    @Schema(type = "String",
            description = "Telefonnummer 1, maxs 20 sifre")
    private String telefonnummer_1;

    @Schema(type = "String",
            description = "Landskode 2 fra kodeverk 'Retningsnumre'")
    private String telefonLandskode_2;

    @Schema(type = "String",
            description = "Telefonnummer 2, maxs 20 sifre")
    private String telefonnummer_2;

    @Size(min = 4, max = 4)
    @Schema(description = "Type av sikkerhetstiltak, alternativer: FYUS, TFUS, FTUS, DIUS og TOAN")
    private String typeSikkerhetTiltak;

    @Size(min = 1, max = 50)
    @Schema(description = "Beskrivelse av type sikkerhetstiltak: \'Fysisk utestengelse\', \'Telefonisk utestengelse\', " +
            "\'Fysisk/telefonisk utestengelse\', \'Digital utestengelse\' og \'To ansatte i samtale\'")
    private String beskrSikkerhetTiltak;

    @Schema(description = "Når sikkerhetstiltaket starter, dato fra-og-med")
    private LocalDateTime sikkerhetTiltakDatoFom;

    @Schema(description = "Når sikkerhetstiltaket opphører, dato til-og-med")
    private LocalDateTime sikkerhetTiltakDatoTom;
}
