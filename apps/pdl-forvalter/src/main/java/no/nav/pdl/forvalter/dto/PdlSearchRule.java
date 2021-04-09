package no.nav.pdl.forvalter.dto;

import lombok.Getter;

@Getter
public enum PdlSearchRule {

    FUZZY("fuzzy"),
    EQUALS("equals"),
    FROM("from"),
    TO("to"),
    CONTAINS("contains");

    PdlSearchRule(String name) {
        this.name = name;
    }

    private final String name;
}
