package no.nav.registre.hodejegeren.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KoderForSivilstand {
    UOPPGITT("0"),
    UGIFT("1"),
    GIFT("2"),
    ENKE_ENKEMANN("3"),
    SKILT("4"),
    SEPARERT("5"),
    REGISTRERT_PARTNER("6"),
    SEPARERT_PARTNER("7"),
    SKILT_PARTNER("8"),
    GJENLEVENDE_PARTNER("9");

    private String sivilstandKode;
}
