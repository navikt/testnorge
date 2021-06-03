package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums.AarsakBeregnetInntektEndringKodeListe;

@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsInntekt {
    @JsonProperty
    private Double beloep;

    @JsonProperty
    private AarsakBeregnetInntektEndringKodeListe aarsakVedEndring;
}
