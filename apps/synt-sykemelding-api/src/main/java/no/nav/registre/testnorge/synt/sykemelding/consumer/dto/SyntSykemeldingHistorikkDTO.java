package no.nav.registre.testnorge.synt.sykemelding.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SyntSykemeldingHistorikkDTO {
    String endringshistorikk;
    List<SyntSykemeldingDTO> sykmeldinger;
}
