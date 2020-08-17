package no.nav.registre.inntekt.provider.rs.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

import no.nav.registre.inntekt.consumer.rs.dokmot.dto.InntektDokumentResponse;

@Value
@RequiredArgsConstructor
public class AltinnInntektResponse {
    @JsonProperty
    private final String fnr;
    @JsonProperty
    private final List<InntektDokumentResponse> dokumenter;
}
