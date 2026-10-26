package no.nav.dolly.domain.resultset.inst;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsInstdata {

    @Schema(description = "Hvilken type institusjon oppholdet er registrert på. "
                    + "Kan være kodeverdiene AS (Alders- og sykehjem), FO (Fengsel) eller HS (Helseinsitusjon)")
    private InstdataInstitusjonstype institusjonstype;

    @Schema(type = "LocalDateTime",
            description = "Dato for begynnelsen av oppholdet")
    private LocalDateTime startdato;

    @Schema(type = "LocalDateTime",
            description = "Dato oppholdet er forventet å være avsluttet")
    private LocalDateTime forventetSluttdato;

    @Schema(type = "LocalDateTime",
            description = "Nytt Api har kun denne for slutt av opphold")
    private LocalDateTime sluttdato;

    @EqualsAndHashCode.Exclude
    @Schema(description = "Id for opphold; brukes ved endring")
    private String oppholdId;

    @EqualsAndHashCode.Exclude
    private Map<String, Set<String>> opprettedeOppholdId;

    public Map<String, Set<String>> getOpprettedeOppholdId() {

        if (isNull(opprettedeOppholdId)) {
            opprettedeOppholdId = new HashMap<>();
        }
        return opprettedeOppholdId;
    }

    @Getter
    public enum InstdataInstitusjonstype {

        AS("Alders- og sykehjem"),
        FO("Fengsel"),
        HS("Helseinsitusjon");

        private final String beskrivelse;

        InstdataInstitusjonstype(String beskrivelse) {
            this.beskrivelse = beskrivelse;
        }
    }
}
