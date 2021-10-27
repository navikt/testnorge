package no.nav.testnav.apps.adresseservice.dto;

import lombok.Getter;

@Getter
public enum PdlSearchRule {

    FUZZY("fuzzy"),
    EQUALS("equals"),
    FROM("from"),
    TO("to"),
    CONTAINS("contains"),
    RANDOM("random"),
    EXISTS("exists");

    PdlSearchRule(String name) {
        this.name = name;
    }

    private final String name;
}
