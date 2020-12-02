package no.nav.registre.testnorge.personexportapi.consumer.dto;

import lombok.Getter;

@Getter
public enum KjoennType {

    K(0,"Kvinne"),
    M(1, "Mann"),
    U(2, "Ukjent");

    private String beskrivelse;
    private Integer hdirType;

    KjoennType(Integer hdirType, String beskrivelse) {
        this.hdirType = hdirType;
        this.beskrivelse = beskrivelse;
    }
}
