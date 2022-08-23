package no.nav.testnav.libs.dto.kontoregisterservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HentKontoResponseDTO {
    private KontoDTO aktivKonto;
    private List<KontoDTO> kontohistorikk;
}
