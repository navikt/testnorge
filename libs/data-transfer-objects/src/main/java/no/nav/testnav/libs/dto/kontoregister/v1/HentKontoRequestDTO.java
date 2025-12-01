package no.nav.testnav.libs.dto.kontoregister.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HentKontoRequestDTO {
    private String kontohaver;
}
