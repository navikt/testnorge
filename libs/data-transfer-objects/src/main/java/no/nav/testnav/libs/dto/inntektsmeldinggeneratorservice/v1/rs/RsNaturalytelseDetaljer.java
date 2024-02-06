package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RsNaturalytelseDetaljer {

    @JsonProperty
    private String naturaytelseType;
    @JsonProperty
    private LocalDate fom;
    @JsonProperty
    private Double beloepPrMnd;
}
