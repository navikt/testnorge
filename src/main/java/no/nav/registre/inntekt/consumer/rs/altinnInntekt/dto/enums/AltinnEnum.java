package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public interface AltinnEnum {

    @JsonValue
    String getValue();
}
