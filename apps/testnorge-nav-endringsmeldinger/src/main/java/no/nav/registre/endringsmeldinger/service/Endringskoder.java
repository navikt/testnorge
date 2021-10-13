package no.nav.registre.endringsmeldinger.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endringskoder {
    TELEFONNUMMER("Z010"),
    NY_ENDRE_SPRAAKKODE("Z510"),
    GIRONR("Z310"),
    OPPHOER_AV_ADRESSE("ZM10"),
    NY_ENDRE_ADRESSE("Z610"),
    UTENLANDSK_PERSONDATA("ZV10"),
    GIRONR_UTLAND("ZD10"),
    ENDRE_NAVN("Z810");

    private String endringskode;
}
