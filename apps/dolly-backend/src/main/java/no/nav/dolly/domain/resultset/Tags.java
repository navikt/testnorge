package no.nav.dolly.domain.resultset;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Tags {

    TEST("Testing");

    private String beskrivelse;

    Tags(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
