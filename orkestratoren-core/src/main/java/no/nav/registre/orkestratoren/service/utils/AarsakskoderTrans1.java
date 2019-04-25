package no.nav.registre.orkestratoren.service.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Årsakskoden og transaksjonskoden er identifiserende for en skdmelding.
 */
@Getter
@AllArgsConstructor
public enum AarsakskoderTrans1 {
    INNVANDRING("02"), FOEDSELSMELDING("01"), FOEDSELSNUMMERKORREKSJON("39"), DOEDSMELDING("43"), UTVANDRING("32");

    private String aarsakskode;

}
