package no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.dto;

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
