package no.nav.registre.inntekt.provider.rs.v2.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@RequiredArgsConstructor
public class AltinnInntektResponseV2 {
    @JsonProperty
    private final String fnr;
    @JsonProperty
    private final List<InntektDokumentResponseV2> dokumenter;
}
