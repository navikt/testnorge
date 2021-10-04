package no.nav.testnav.apps.persontilgangservice.client.maskinporten.v1.dto;

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
