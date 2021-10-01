package no.nav.testnav.apps.tilgangservice.domain;

public record AccessToken(String value) {
    public AccessToken(no.nav.testnav.apps.tilgangservice.client.maskinporten.v1.dto.AccessToken accessToken) {
        this(accessToken.accessToken());
    }
}
