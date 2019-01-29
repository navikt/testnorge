package no.nav.registre.orkestratoren.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyntetiserInntektsmeldingRequest {

    @JsonProperty("avspillergruppeId")
    private long avspillergruppeId;
}
