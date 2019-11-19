package no.nav.dolly.domain.resultset.inst;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsInstdata {

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Hvilken type institusjon oppholdet er registrert på. Kan være kodeverdiene AS (Alders- og sykehjem), FO (Fengsel) eller HS (Helseinsitusjon)"
    )
    private InstdataInstitusjonstype institusjonstype;

    @ApiModelProperty(
            position = 2,
            required = true,
            dataType = "LocalDateTime",
            value = "Dato for begynnelsen av oppholdet"
    )
    private LocalDateTime startdato;

    @ApiModelProperty(
            position = 3,
            dataType = "LocalDateTime",
            value = "Dato oppholdet er forventet å være avsluttet"

    )
    private LocalDateTime forventetSluttdato;

    @ApiModelProperty(
            position = 4,
            dataType = "LocalDateTime",
            value = "Dato oppholdet var avsluttet"
    )
    private LocalDateTime faktiskSluttdato;

    @ApiModelProperty(
            position = 5,
            value = "Kodeverdi for oppholdskategori. Kan være kodeverdiene A (Alders- og sykehjem), D (Dagpasient), F (Ferieopphold), "
                    + "H (Heldøgnpasient), P (Fødsel), R (Opptreningsinstitusjon), S (Soningsfange) eller V (Varetektsfange)"
    )
    private InstdataKategori kategori;

    @ApiModelProperty(
            position = 6,
            value = "Kodeverdi for kilde. Defaultverdi settes i backend basert på institusjonstype hvis ikke angitt. "+
                    "(Kan være kodeverdiene APPBRK(Applikasjonsbruker), IT(Infotrygd), AINN(Altinn), PP01(Pensjon), TPS(TPS), INST(INST))"
    )
    private InstdataKilde kilde;

    @ApiModelProperty(
            position = 7,
            value = "Settes i backend basert på institusjonstype hvis ikke utfylt (Unik id for en institusjon i TSS, bruk oppslag-api’et for å finne den)"
    )
    private String tssEksternId;

    @ApiModelProperty(
            position = 8,
            value = "Flag for å markere om personen ble overført til en annen institusjon"
    )
    private Boolean overfoert;
}
