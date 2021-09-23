package no.nav.registre.skd.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum KoderForSivilstand {
    UOPPGITT("0", "Uoppgitt"),
    UGIFT("1", "Ugift"),
    GIFT("2", "Gift"),
    ENKE_ENKEMANN("3", "Enke/-mann"),
    SKILT("4", "Skilt"),
    SEPARERT("5", "Separert"),
    REGISTRERT_PARTNER("6", "Registrert partner"), // sjekk denne
    SEPARERT_PARTNER("7", "Separert partner"), // sjekk denne
    SKILT_PARTNER("8", "Skilt partner"), // sjekk denne
    GJENLEVENDE_PARTNER("9", "GjenlevendePartner"); // sjekk denne

    private String sivilstandKodeSKD;
    private String sivilstandKode;

    public List<String> getAlleSivilstandkodene() {
        return Arrays.asList(sivilstandKodeSKD, sivilstandKode);
    }
}
