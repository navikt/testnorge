package no.nav.registre.inntekt.provider.rs.v2.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InntektDokumentResponseV2 {
    @JsonProperty
    String journalpostId;
    @JsonProperty
    Set<String> dokumentInfoIder;
    @JsonProperty
    String xml;
}
