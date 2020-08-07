package no.nav.registre.testnorge.rapportering.consumer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class Response {
    Boolean ok;
    String response_metadata;
}