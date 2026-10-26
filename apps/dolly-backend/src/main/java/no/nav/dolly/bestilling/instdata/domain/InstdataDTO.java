package no.nav.dolly.bestilling.instdata.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstdataDTO {

    private String norskident;
    private String institusjonsnummer;

    private InstdataInstitusjonstype institusjonstype;
    private String oppholdstype;

    private LocalDate startdato;
    private LocalDate sluttdato;
    private LocalDate forventetSluttdato;

    private String registrertAv;

    @EqualsAndHashCode.Exclude
    private String oppholdId;

    @EqualsAndHashCode.Exclude
    private Map<String, Set<String>> opprettedeOppholdId;

    public Map<String, Set<String>> getOpprettedeOppholdId() {

        if (isNull(opprettedeOppholdId)) {
            opprettedeOppholdId = new HashMap<>();
        }
        return opprettedeOppholdId;
    }

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
