package no.nav.testnav.libs.securitycore.domain.tokenx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WellKnown {

    @JsonProperty
    String token_endpoint;

    @JsonProperty
    String end_session_endpoint;
}
