package no.nav.testnav.libs.securitycore.domain.tokenx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WellKnown {

    @JsonProperty("token_endpoint")
    private String tokenEndpoint;

    @JsonProperty("end_session_endpoint")
    private String endSessionEndpoint;
}
