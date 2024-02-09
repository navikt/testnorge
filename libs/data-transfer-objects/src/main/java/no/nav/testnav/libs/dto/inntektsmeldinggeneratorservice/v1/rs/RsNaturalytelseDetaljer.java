package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RsNaturalytelseDetaljer {

    private String naturalytelseType;
    private LocalDateTime fom;
    private Double beloepPrMnd;
}
