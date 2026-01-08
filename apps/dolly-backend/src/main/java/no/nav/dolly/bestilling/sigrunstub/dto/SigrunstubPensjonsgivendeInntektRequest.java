package no.nav.dolly.bestilling.sigrunstub.dto;

import tools.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SigrunstubPensjonsgivendeInntektRequest extends SigrunstubRequest {

    private String norskident;
    private JsonNode pensjonsgivendeInntekt;
}
