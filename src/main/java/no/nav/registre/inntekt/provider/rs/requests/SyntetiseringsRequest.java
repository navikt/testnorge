package no.nav.registre.inntekt.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SyntetiseringsRequest {

    @JsonProperty("avspillergruppeId")
    private long avspillergruppeId;
}
