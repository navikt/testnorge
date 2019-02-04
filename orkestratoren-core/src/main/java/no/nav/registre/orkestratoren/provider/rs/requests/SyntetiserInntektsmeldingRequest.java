package no.nav.registre.orkestratoren.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SyntetiserInntektsmeldingRequest {

    @JsonProperty("avspillergruppeId")
    private long avspillergruppeId;
}
