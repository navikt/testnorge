package no.nav.testnav.apps.syntvedtakshistorikkservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public enum Tags {
    ARENASYNT("ARENASYNT"),
    DOLLY("DOLLY");

    private String beskrivelse;

    Tags(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
