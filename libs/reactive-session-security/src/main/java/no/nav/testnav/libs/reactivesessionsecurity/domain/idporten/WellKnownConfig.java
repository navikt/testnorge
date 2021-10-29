package no.nav.testnav.libs.reactivesessionsecurity.domain.idporten;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WellKnownConfig {

    @JsonProperty("token_endpoint")
    String tokenEndpoint;

    @JsonProperty("end_session_endpoint")
    String endSessionEndpoint;
}
