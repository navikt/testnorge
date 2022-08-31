package no.nav.testnav.libs.dto.kontoregisterservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KontoregisterResponseDTO {
    private String miljoe;

    private String status;
    private String melding;
    private String utfyllendeMelding;
}
