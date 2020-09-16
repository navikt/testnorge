package no.nav.registre.testnorge.libs.oauth2.domain;

import lombok.Data;

@Data
public class ClientCredential {
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
