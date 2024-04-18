package no.nav.dolly.bestilling.inntektsmelding.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaksjonMappingDTO {

    private InntektsmeldingRequest request;
    private InntektsmeldingResponse.Dokument dokument;
}
