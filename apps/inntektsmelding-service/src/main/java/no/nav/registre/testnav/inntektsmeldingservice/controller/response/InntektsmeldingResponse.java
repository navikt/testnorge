package no.nav.registre.testnav.inntektsmeldingservice.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@RequiredArgsConstructor
public class InntektsmeldingResponse {
    @JsonProperty
    private final String fnr;
    @JsonProperty
    private final List<InntektDokumentResponse> dokumenter;
}
