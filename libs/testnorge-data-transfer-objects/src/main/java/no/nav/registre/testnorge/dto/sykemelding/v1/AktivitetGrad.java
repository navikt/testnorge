package no.nav.registre.testnorge.dto.sykemelding.v1;


import lombok.Getter;

@Getter
public enum AktivitetGrad {
    INGEN,
    GRADERT_20(20, false),
    GRADERT_40(40, false),
    GRADERT_50(50, false),
    GRADERT_60(60, false),
    GRADERT_80(80, false),
    AVVENTENDE,
    GRADERT_REISETILSKUDD(60, true),
    BEHANDLINGSDAGER(4),
    BEHANDLINGSDAG(1),
    REISETILSKUDD(true);

    private final Integer grad;
    private final Boolean reisetilskudd;
    private final Integer behandlingsdager;

    AktivitetGrad(Integer grad, Boolean reisetilskudd, Integer behandlingsdager) {
        this.grad = grad;
        this.reisetilskudd = reisetilskudd;
        this.behandlingsdager = behandlingsdager;
    }

    AktivitetGrad(Boolean reisetilskudd) {
        this(null, reisetilskudd, null);
    }

    AktivitetGrad(Integer grad, Boolean reisetilskudd) {
        this(grad, reisetilskudd, null);
    }

    AktivitetGrad(Integer behandlingsdager) {
        this(null, null, behandlingsdager);
    }

    AktivitetGrad() {
        this(null, null, null);
    }
}
