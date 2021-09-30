package no.nav.testnav.apps.tilgangservice.config;

import java.util.List;

public record WellKnown(
        String issuer,
        String token_endpoint,
        String jwks_uri,
        List<String> token_endpoint_auth_methods_supported,
        List<String> grant_types_supported
) {

}
