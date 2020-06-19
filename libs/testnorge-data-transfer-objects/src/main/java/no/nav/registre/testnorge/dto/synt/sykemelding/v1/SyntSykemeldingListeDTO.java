package no.nav.registre.testnorge.dto.synt.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SyntSykemeldingListeDTO {
    List<SyntSykemeldingDTO> liste;
}
