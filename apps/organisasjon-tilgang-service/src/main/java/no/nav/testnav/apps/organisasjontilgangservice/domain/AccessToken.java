package no.nav.testnav.apps.organisasjontilgangservice.domain;

public record AccessToken(String value) {
    public AccessToken(no.nav.testnav.apps.organisasjontilgangservice.consumer.maskinporten.v1.dto.AccessToken accessToken) {
        this(accessToken.accessToken());
    }
}
