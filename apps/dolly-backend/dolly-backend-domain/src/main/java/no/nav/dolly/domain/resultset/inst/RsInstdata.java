package no.nav.dolly.domain.resultset.inst;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsInstdata {

    @Schema(required = true,
            description = "Hvilken type institusjon oppholdet er registrert på. "
                    + "Kan være kodeverdiene AS (Alders- og sykehjem), FO (Fengsel) eller HS (Helseinsitusjon)")
    private InstdataInstitusjonstype institusjonstype;

    @Schema(required = true,
            type = "LocalDateTime",
            description = "Dato for begynnelsen av oppholdet")
    private LocalDateTime startdato;

    @Schema(type = "LocalDateTime",
            description = "Dato oppholdet er forventet å være avsluttet")
    private LocalDateTime forventetSluttdato;

    @Schema(type = "LocalDateTime",
            description = "Dato oppholdet var avsluttet")
    private LocalDateTime faktiskSluttdato;

    @Schema(type = "LocalDateTime",
            description = "Nytt Api har kun denne for slutt av opphold")
    private LocalDateTime sluttdato;

    @Schema(description = "Kodeverdi for oppholdskategori. "
            + "Kan være kodeverdiene A (Alders- og sykehjem), D (Dagpasient), F (Ferieopphold), "
            + "H (Heldøgnpasient), P (Fødsel), R (Opptreningsinstitusjon), S (Soningsfange) eller V (Varetektsfange)")
    private InstdataKategori kategori;

    @Schema(description = "Kodeverdi for kilde. "
            + "Defaultverdi settes i backend basert på institusjonstype hvis ikke angitt. "
            + "(Kan være kodeverdiene APPBRK(Applikasjonsbruker), "
            + "IT(Infotrygd), AINN(Altinn), PP01(Pensjon), TPS(TPS), INST(INST))")
    private InstdataKilde kilde;

    @Schema(description = "Settes i backend basert på institusjonstype hvis ikke utfylt "
            + "(Unik id for en institusjon i TSS, bruk oppslag-api’et for å finne den)")
    private String tssEksternId;

    @Schema(description = "Flag for å markere om personen ble overført til en annen institusjon")
    private Boolean overfoert;
}
