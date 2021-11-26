package no.nav.dolly.domain.resultset.inst;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
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
