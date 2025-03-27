package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsNaturalytelseDetaljer {

    private String naturalytelseType;
    private String fom;
    private Double beloepPrMnd;
}
