package no.nav.dolly.domain.resultset.inst;

import lombok.Getter;

@Getter
public enum InstdataVarighet {

    L("Langvarig"),
    K("Kortvarig"),
    U("Ubestemt");

    private String beskrivelse;

    InstdataVarighet(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
