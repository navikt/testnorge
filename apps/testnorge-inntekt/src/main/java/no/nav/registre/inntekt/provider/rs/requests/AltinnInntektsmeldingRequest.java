package no.nav.registre.inntekt.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.RsInntektsmeldingRequest;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.RsJoarkMetadata;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AltinnInntektsmeldingRequest {
    @JsonProperty
    private String miljoe;
    @JsonProperty
    private String arbeidstakerFnr;  // TODO: ident! (i v2)
    @JsonProperty
    private RsJoarkMetadata joarkMetadata;
    @JsonProperty
    private List<RsInntektsmeldingRequest> inntekter;
}
