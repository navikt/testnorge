package no.nav.testnav.altinn3tilgangservice.domain;

public record AccessToken(String value) {
    public AccessToken(no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.v1.dto.AccessToken accessToken) {
        this(accessToken.accessToken());
    }
}
