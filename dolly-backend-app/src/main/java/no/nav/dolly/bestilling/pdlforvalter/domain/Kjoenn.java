package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.Getter;

@Getter
public enum Kjoenn {
    MANN("M"),
    KVINNE("K"),
    UKJENT("U");

    Kjoenn(String kode) {
        this.kode = kode;
    }

    private String kode;

    public static Kjoenn decode(String kode) {

        for (Kjoenn kjoenn : Kjoenn.values()) {
            if (kjoenn.getKode().equals(kode)) {
                return kjoenn;
            }
        }
        return null;
    }
}
