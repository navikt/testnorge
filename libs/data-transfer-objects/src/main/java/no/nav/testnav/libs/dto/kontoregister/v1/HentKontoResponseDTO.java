package no.nav.testnav.libs.dto.kontoregister.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HentKontoResponseDTO {

    private HttpStatus status;
    private KontoDTO aktivKonto;
}
