package no.nav.registre.inntekt.provider.rs.v1.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InntektDokumentResponseV1 {
    @JsonProperty
    String journalpostId;
    @JsonProperty
    String dokumentInfoId;
    @JsonProperty
    String xml;
}
