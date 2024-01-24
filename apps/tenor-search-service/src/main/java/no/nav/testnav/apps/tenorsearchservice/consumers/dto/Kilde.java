package no.nav.testnav.apps.tenorsearchservice.consumers.dto;

import lombok.Getter;

@Getter
public enum Kilde {

    FREG("freg"),
    FORETAKSREGISTRET("brreg-er-fr");

    private final String tenorKilde;

    Kilde(String tenorKilde) {
        this.tenorKilde = tenorKilde;
    }
}
