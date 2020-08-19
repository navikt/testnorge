package no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.enums;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.nav.registre.inntekt.utils.JsonAltinnEnumSerializer;

@JsonSerialize(using = JsonAltinnEnumSerializer.class)
public interface AltinnEnum {
    String getValue();
}
