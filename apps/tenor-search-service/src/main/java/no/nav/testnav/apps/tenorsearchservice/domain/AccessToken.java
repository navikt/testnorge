package no.nav.testnav.apps.tenorsearchservice.domain;

public record AccessToken(String value) {
    public AccessToken(no.nav.testnav.apps.tenorsearchservice.consumers.dto.AccessToken accessToken) {
        this(accessToken.accessToken());
    }
}
