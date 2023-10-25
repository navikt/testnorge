package no.nav.dolly.domain.resultset.inst;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsInstdata {

    @Schema(
            description = "Hvilken type institusjon oppholdet er registrert på. "
                    + "Kan være kodeverdiene AS (Alders- og sykehjem), FO (Fengsel) eller HS (Helseinsitusjon)")
    private InstdataInstitusjonstype institusjonstype;

    @Schema(
            type = "LocalDateTime",
            description = "Dato for begynnelsen av oppholdet")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime startdato;

    @Schema(type = "LocalDateTime",
            description = "Dato oppholdet er forventet å være avsluttet")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime forventetSluttdato;

    @Schema(type = "LocalDateTime",
            description = "Dato oppholdet var avsluttet")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime faktiskSluttdato;

    @Schema(type = "LocalDateTime",
            description = "Nytt Api har kun denne for slutt av opphold")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
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
