package no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record WellKnown(
        String issuer,
        @JsonProperty("token_endpoint")
        String tokenEndpoint,
        @JsonProperty("jwks_uri")
        String jwksUri,
        @JsonProperty("token_endpoint_auth_methods_supported")
        List<String> tokenEndpointAuthMethodsSupported,
        @JsonProperty("grant_types_supported")
        List<String> grantTypesSupported
) {
}
