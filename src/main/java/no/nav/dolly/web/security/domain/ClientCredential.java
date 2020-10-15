package no.nav.dolly.web.security.domain;

import lombok.Data;

@Data
public abstract class ClientCredential {
    final String clientId;
    final String clientSecret;

    @Override
    public final String toString() {
        return "ClientCredential{" +
                "clientId=[HIDDEN]" +
                ", clientSecret=[HIDDEN]" +
                '}';
    }
}
