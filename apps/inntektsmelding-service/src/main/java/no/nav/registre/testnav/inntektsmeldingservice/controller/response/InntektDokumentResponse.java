package no.nav.registre.testnav.inntektsmeldingservice.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InntektDokumentResponse {
    @JsonProperty
    String journalpostId;
    @JsonProperty
    String dokumentInfoId;
    @JsonProperty
    String xml;
}
