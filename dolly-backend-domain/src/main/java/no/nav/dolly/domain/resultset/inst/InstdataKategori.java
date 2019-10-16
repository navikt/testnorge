package no.nav.dolly.domain.resultset.inst;

import lombok.Getter;

@Getter
public enum InstdataKategori {

    A ("Alders- og sykehjem"),
    D ("Dagpasient"),
    F ("Ferieopphold"),
    H ("Heldøgnpasient"),
    P ("Fødsel"),
    R ("Opptreningsinstitusjon"),
    S ("Soningsfange"),
    V ("Varetektsfange");

    private String beskrivelse;

    InstdataKategori(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
