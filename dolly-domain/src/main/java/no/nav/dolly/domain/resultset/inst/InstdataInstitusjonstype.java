package no.nav.dolly.domain.resultset.inst;

import lombok.Getter;

@Getter
public enum InstdataInstitusjonstype {

    AS("Alders- og sykehjem"),
    FO("Fengsel"),
    HS("Helseinsitusjon");

    private String beskrivelse;

    InstdataInstitusjonstype(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
