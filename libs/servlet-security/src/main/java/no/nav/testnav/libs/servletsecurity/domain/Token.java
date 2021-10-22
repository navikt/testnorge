package no.nav.testnav.libs.servletsecurity.domain;

public record Token(String value, String id, boolean clientCredentials) {
}
