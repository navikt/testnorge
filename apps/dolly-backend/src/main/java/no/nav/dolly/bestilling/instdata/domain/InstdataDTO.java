package no.nav.dolly.bestilling.instdata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

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

    @Transient
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private boolean eksisterende;

    public enum InstdataInstitusjonstype {

        AS("Alders- og sykehjem"),
        FO("Fengsel"),
        HS("Helseinsitusjon");

        InstdataInstitusjonstype(String beskrivelse) {
        }
    }
}
