package no.nav.testnav.libs.dto.skattekortservice.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tabelltype {

    TREKKTABELL_FOR_PENSJON("trekktabellForPensjon"),
    TREKKTABELL_FOR_LOENN("trekktabellForLoenn");

    private final String value;
}
