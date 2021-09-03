package no.nav.testnav.libs.reactivesecurity.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WellKnownConfig {
    @JsonProperty("token_endpoint")
    String tokenEndpoint;
}
