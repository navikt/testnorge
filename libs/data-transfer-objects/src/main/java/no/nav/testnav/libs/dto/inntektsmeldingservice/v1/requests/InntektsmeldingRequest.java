package no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

import no.nav.testnav.libs.dto.dokarkiv.v1.RsJoarkMetadata;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class InntektsmeldingRequest {
    @JsonProperty
    private String miljoe;
    @JsonProperty
    private String arbeidstakerFnr;  // TODO: ident! (i v2)
    @JsonProperty
    private RsJoarkMetadata joarkMetadata;
    @JsonProperty
    private List<RsInntektsmeldingRequest> inntekter;
}
