package no.nav.dolly.bestilling.etterlatte.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.etterlatte.EtterlatteYtelse;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VedtakRequestDTO {

    private EtterlatteYtelse.YtelseType ytelse;
    private String avdoed;
    private String gjenlevende;
    private List<String> barn;
}
