package no.nav.testnav.libs.dto.skattekortservice.v1;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tabelltype {

    TREKKTABELL_FOR_PENSJON("trekktabellForPensjon"),
    TREKKTABELL_FOR_LOENN("trekktabellForLoenn");

    @JsonValue
    private final String value;

    @Override
    public String toString() {
        return this.name() + "," + value;
    }
}
