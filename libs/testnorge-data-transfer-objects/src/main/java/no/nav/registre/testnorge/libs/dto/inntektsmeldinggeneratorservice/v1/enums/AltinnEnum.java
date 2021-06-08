package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = JsonAltinnEnumSerializer.class)
public interface AltinnEnum {
    String getValue();
}
