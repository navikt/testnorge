package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums.AarsakBeregnetInntektEndringKodeListe;

@Value
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
public class RsBeregnetInntekt {
    @JsonProperty
    private Double beloep;
    @JsonProperty
    private AarsakBeregnetInntektEndringKodeListe aarsakVedEndring;
}
