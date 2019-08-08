package no.nav.dolly.domain.resultset.inst;

import lombok.Getter;

@Getter
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
