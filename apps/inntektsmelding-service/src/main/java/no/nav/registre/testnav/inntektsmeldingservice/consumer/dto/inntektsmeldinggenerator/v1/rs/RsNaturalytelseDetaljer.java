package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums.NaturalytelseKodeListe;

@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsNaturalytelseDetaljer {

    @JsonProperty
    private NaturalytelseKodeListe naturalytelseType;

    @JsonProperty
    private LocalDate fom;

    @JsonProperty
    private Double beloepPrMnd;

}
