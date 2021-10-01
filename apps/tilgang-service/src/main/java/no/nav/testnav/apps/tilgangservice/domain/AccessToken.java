package no.nav.testnav.apps.tilgangservice.domain;

public record AccessToken(
        String access_token,
        String token_type,
        Integer expires_in,
        String scope
) {

}
