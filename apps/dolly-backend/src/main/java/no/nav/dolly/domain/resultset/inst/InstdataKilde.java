package no.nav.dolly.domain.resultset.inst;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum InstdataKilde {

    APPBRK("Applikasjonsbruker"),
    IT("Infotrygd"),
    AINN("Altinn"),
    PP01("Pensjon"),
    TPS("TPS"),
    INST("INST");

    private String beskrivelse;

    InstdataKilde(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
