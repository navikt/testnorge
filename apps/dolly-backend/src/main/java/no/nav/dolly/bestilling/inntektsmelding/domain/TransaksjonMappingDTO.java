package no.nav.dolly.bestilling.inntektsmelding.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaksjonMappingDTO {

    private InntektsmeldingRequest request;
    private InntektsmeldingResponse.Dokument dokument;
}
