package no.nav.registre.inntekt.provider.rs.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@RequiredArgsConstructor
public class AltinnInntektResponseV1 {
    @JsonProperty
    private final String fnr;
    @JsonProperty
    private final List<InntektDokumentResponseV1> dokumenter;
}
