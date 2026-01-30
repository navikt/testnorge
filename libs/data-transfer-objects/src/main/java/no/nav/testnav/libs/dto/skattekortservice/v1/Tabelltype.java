package no.nav.testnav.libs.dto.skattekortservice.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Tabelltype {

    TREKKTABELL_FOR_PENSJON("trekktabellForPensjon"),
    TREKKTABELL_FOR_LOENN("trekktabellForLoenn");

    @JsonValue
    private final String value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Tabelltype(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.name() + "," + value;
    }
}
