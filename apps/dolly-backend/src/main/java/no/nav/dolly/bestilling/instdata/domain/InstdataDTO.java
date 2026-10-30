package no.nav.dolly.bestilling.instdata.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstdataDTO {

    private String norskident;
    private String institusjonsnummer;
    private String tssEksternId;

    private InstdataInstitusjonstype institusjonstype;
    private String oppholdstype;

    private LocalDate startdato;
    private LocalDate sluttdato;
    private LocalDate forventetSluttdato;

    private String registrertAv;

    @EqualsAndHashCode.Exclude
    private String oppholdId;

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
