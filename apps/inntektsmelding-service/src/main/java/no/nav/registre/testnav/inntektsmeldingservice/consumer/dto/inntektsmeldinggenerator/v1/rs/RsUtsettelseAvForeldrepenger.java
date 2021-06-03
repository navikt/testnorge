package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums.AarsakUtsettelseKodeListe;

@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsUtsettelseAvForeldrepenger {

    @JsonProperty
    private RsPeriode periode;
    @JsonProperty
    private AarsakUtsettelseKodeListe aarsakTilUtsettelse;

}
