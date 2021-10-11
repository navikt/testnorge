package no.nav.testnav.apps.persontilgangservice.domain;

public record AccessToken(String value) {
    public AccessToken(no.nav.testnav.apps.persontilgangservice.client.maskinporten.v1.dto.AccessToken accessToken) {
        this(accessToken.accessToken());
    }
}
