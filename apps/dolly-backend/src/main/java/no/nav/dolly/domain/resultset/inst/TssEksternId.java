package no.nav.dolly.domain.resultset.inst;

import lombok.Getter;

@Getter
public enum TssEksternId {

    ADAMSTUEN_SYKEHJEM("80000464106"),
    INDRE_OSTFOLD_FENGSEL("80000465653"),
    HELGELANDSSYKEHUSET_HF("80000464241");

    TssEksternId(String id) {
        this.id = id;
    }

    private String id;
}
