package no.nav.registre.testnorge.libs.dto.syntrest.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FartoeyDTO {
    String skipsregister;
    String skipstype;
    String fartsomraade;
}
