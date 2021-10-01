package no.nav.testnav.apps.tilgangservice.client.maskinporten.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessToken(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("expires_in")
        Integer expiresIn,
        @JsonProperty("scope")
        String scope
) {

}
