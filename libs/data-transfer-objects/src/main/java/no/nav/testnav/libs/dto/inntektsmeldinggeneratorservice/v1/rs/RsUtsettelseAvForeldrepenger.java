package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsUtsettelseAvForeldrepenger {

    private RsPeriode periode;
    private String aarsakTilUtsettelse;
}
