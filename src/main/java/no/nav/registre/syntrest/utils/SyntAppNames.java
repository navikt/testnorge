package no.nav.registre.syntrest.utils;

import lombok.Getter;

@Getter
public enum SyntAppNames {
    AAREG("synthdata-aareg"),
    BISYS("synthdata-arena-bisys"),
    MELDEKORT("synthdata-arena-meldekort"),
    AAP("synthdata-arena-aap"),
    INST("synthdata-inst"),
    INNTEKT("synthdata-inntekt"),
    MEDL("synthdata-medl"),
    EIA("synthdata-eia"),
    POPP("synthdata-popp"),
    TP("synthdata-tp"),
    TPS("synthdata-tps"),
    NAV("synthdata-nav"),
    PEN("synthdata-pen"),
    BA("synthdata-ba"),
    SAM("synthdata-sam");

    private String name;
    SyntAppNames(String name) {
        this.name = name;
    }
}
