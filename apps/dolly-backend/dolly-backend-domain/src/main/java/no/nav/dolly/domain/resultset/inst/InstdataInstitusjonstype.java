package no.nav.dolly.domain.resultset.inst;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum InstdataInstitusjonstype {

    AS("Alders- og sykehjem"),
    FO("Fengsel"),
    HS("Helseinsitusjon");

    private String beskrivelse;

    InstdataInstitusjonstype(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
