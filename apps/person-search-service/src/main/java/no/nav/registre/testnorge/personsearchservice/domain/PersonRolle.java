package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum PersonRolle {
    BARN("BARN"),
    MOR("MOR"),
    MEDMOR("MEDMOR"),
    FAR("FAR");

    private String beskrivelse;

    PersonRolle(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    @Override
    public String toString() {
        return beskrivelse;
    }
}
