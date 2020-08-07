package no.nav.registre.testnorge.rapportering.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Response {
    Boolean ok;
    String response_metadata;
}