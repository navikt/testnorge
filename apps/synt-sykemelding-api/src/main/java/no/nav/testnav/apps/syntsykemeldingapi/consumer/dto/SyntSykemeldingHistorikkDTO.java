package no.nav.testnav.apps.syntsykemeldingapi.consumer.dto;

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
