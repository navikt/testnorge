package no.nav.registre.inntekt.consumer.rs.dokmot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InntektDokumentResponse {
    @JsonProperty
    private final String journalpostId;
    @JsonProperty
    private final String dokumentInfoId;
    @JsonProperty
    private final String xml;
}
