package no.nav.registre.testnav.inntektsmeldingservice.controller.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.dovarkiv.v1.RsJoarkMetadata;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.RsInntektsmeldingRequest;


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
