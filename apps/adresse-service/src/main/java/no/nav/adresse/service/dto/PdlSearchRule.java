package no.nav.adresse.service.dto;

import lombok.Getter;

@Getter
public enum PdlSearchRule {

    FUZZY("fuzzy"),
    EQUALS("equals"),
    FROM("from"),
    TO("to");

    PdlSearchRule(String name) {
        this.name = name;
    }

    private final String name;
}
