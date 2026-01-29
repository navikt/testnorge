package no.nav.dolly.bestilling.skattekort.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultatForSkattekort {

    SKATTEKORTOPPLYSNINGER_OK("skattekortopplysningerOK"),
    IKKE_SKATTEKORT("ikkeSkattekort"),
    IKKE_TREKKPLIKT("ikkeTrekkplikt");

    @JsonValue
    private final String value;
}
