package no.nav.registre.testnorge.personexportapi.consumer.dto;

import lombok.Getter;

@Getter
public enum KjoennType {

    K("Kvinne"),
    M("Mann"),
    U("Ukjent");

    private String beskrivelse;

    KjoennType(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
