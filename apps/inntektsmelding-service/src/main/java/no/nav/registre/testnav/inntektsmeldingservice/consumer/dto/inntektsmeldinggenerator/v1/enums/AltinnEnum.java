package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import no.nav.registre.testnav.inntektsmeldingservice.domain.JsonAltinnEnumSerializer;

@JsonSerialize(using = JsonAltinnEnumSerializer.class)
public interface AltinnEnum {
    String getValue();
}
