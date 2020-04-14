package no.nav.registre.testnorge.elsam.consumer.rs.response.tss;

import lombok.Getter;

@Getter
public enum IdentKode {

    FNR("FNR"),
    HPR("HPR");

    private String kodeNavn;

    IdentKode(String kode) {
        kodeNavn = kode;
    }

}
