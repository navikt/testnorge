package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = JsonAltinnEnumSerializer.class)
public interface AltinnEnum {
    String getValue();
}
